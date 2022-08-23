package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.tag.entity.QTag.*;
import static com.meoguri.linkocean.util.querydsl.CustomPath.*;
import static com.meoguri.linkocean.util.querydsl.JoinInfoBuilder.Initializer.*;
import static com.querydsl.sql.SQLExpressions.*;
import static org.apache.commons.lang3.BooleanUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.util.querydsl.CustomPath;
import com.meoguri.linkocean.util.querydsl.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

@Repository
public class CustomBookmarkRepositoryImpl extends Querydsl4RepositorySupport implements CustomBookmarkRepository {

	public CustomBookmarkRepositoryImpl() {
		super(Bookmark.class);
	}

	/* 대상의 프로필 id 로 북마크 페이징 조회 */
	@Override
	public Page<Bookmark> findByTargetProfileId(final BookmarkFindCond findCond, final Pageable pageable) {
		/* cond 풀기 */
		final Long targetProfileId = findCond.getTargetProfileId();
		final Category category = findCond.getCategory();
		final boolean isFavorite = toBoolean(findCond.getFavorite());
		final List<String> tags = findCond.getTags();
		final String title = findCond.getTitle();
		final OpenType openType = findCond.getOpenType();

		/* 태그는 북마크와 many-many 로 이어져 있기 때문에 별도 쿼리를 통해 북마크 아이디를 셋업 */
		final List<Long> bookmarkIds = getBookmarkIds(tags);
		/* 즐겨찾기 요청이라면 작성자 id 기준 필터링이 없다 */
		final Long writerId = toBoolean(isFavorite) ? null : targetProfileId;

		/* 페이지 쿼리 */
		return applyPagination(
			convertBookmarkSort(pageable),
			selectFrom(bookmark),
			joinIfs(
				joinIf(category != null,
					() -> join(bookmark.writer).fetchJoin()
						.join(bookmark.linkMetadata).fetchJoin()),
				joinIf(tags != null,
					() -> join(bookmark.writer).fetchJoin())
			),
			where(
				titleContains(title),
				categoryEq(category),
				writerIdEq(writerId),
				bookmarkIdsIn(bookmarkIds),
				bookmarkIdsIn(getFavoriteBookmarkIds(isFavorite, targetProfileId)),
				availableByOpenType(openType),
				registered()
			),
			Bookmark::getTagNames
		);
	}

	/* 피드 북마크 조회 */
	@Override
	public Page<Bookmark> findBookmarks(final BookmarkFindCond findCond, final Pageable pageable) {
		/* cond 풀기 */
		final long currentUserProfileId = findCond.getCurrentUserProfileId();
		final Category category = findCond.getCategory();
		final String title = findCond.getTitle();
		final boolean isFavorite = toBoolean(findCond.getFavorite());
		final boolean isFollow = toBoolean(findCond.getFollow());
		final List<String> tags = findCond.getTags();

		/* 태그는 북마크와 many-many 로 이어져 있기 때문에 별도 쿼리를 통해 북마크 아이디를 셋업 */
		final List<Long> bookmarkIds = getBookmarkIds(tags);
		/* 즐겨찾기는 엔티티가 아니라 join 이 아닌 별도 쿼리를 통해 북마크 아이디를 셋업 */
		final List<Long> favoriteBookmarkIds = getFavoriteBookmarkIds(isFavorite, currentUserProfileId);

		/* 페이지 쿼리 */
		return applyPagination(
			convertBookmarkSort(pageable),
			selectFrom(bookmark),
			joinIfs(
				joinIf(category != null,
					() -> join(bookmark.writer).fetchJoin()
						.join(bookmark.linkMetadata).fetchJoin()),

				joinIf(tags != null,
					() -> join(bookmark.writer).fetchJoin())
			),
			where(
				titleContains(title),
				categoryEq(category),
				bookmarkIdsIn(bookmarkIds),
				bookmarkIdsIn(favoriteBookmarkIds),
				followedBy(isFollow, currentUserProfileId),
				availableByOpenType(currentUserProfileId),
				registered()
			),
			Bookmark::getTagNames
		);

	}

	private List<Long> getFavoriteBookmarkIds(final boolean isFavorite, final long profileId) {
		return !isFavorite ? null : getJpasqlQuery()
			.select(bookmarkId)
			.from(favorite)
			.where(CustomPath.profileId.eq(profileId))
			.fetch();
	}

	/* 태그를 포함한 북마크의 id 를 역으로 조회 */
	private List<Long> getBookmarkIds(final List<String> tags) {
		return tags != null ? getJpasqlQuery().select(bt_bookmarkId)
			.distinct()
			.from(bookmark_tag)
			.join(tag)
			.where(tag.name.in(tags))
			.fetch() : null;

	}

	private BooleanBuilder titleContains(final String title) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(title));
	}

	private BooleanBuilder categoryEq(final Category category) {
		return nullSafeBuilder(() -> bookmark.category.eq(category));
	}

	private BooleanBuilder writerIdEq(final Long writerId) {
		return nullSafeBuilder(() -> bookmark.writer.id.eq(writerId));
	}

	private BooleanBuilder bookmarkIdsIn(final List<Long> bookmarkIds) {
		return nullSafeBuilder(() -> bookmark.id.in(bookmarkIds));
	}

	private BooleanBuilder followedBy(boolean isFollow, long currentUserProfileId) {
		return isFollow ? followedBy(currentUserProfileId) : new BooleanBuilder();
	}

	private BooleanBuilder followedBy(long currentUserProfileId) {
		return nullSafeBuilder(() -> bookmark.writer.in(
			select(follow.id.followee)
				.from(follow)
				.where(follow.id.follower.id.eq(currentUserProfileId))
		));
	}

	// 작성자 id 대상 북마크 조회에서 사용
	private BooleanBuilder availableByOpenType(final OpenType openType) {
		// PRIVATE 이상을 조회 하는 요청이므로 필터링이 필요 없음
		if (openType == OpenType.PRIVATE) {
			return new BooleanBuilder();
		}

		// 주어진 openType 이하의 모든 openType 을 조회 할 필요가 있음
		return nullSafeBuilder(() -> bookmark.openType.loe(openType));
	}

	// 피드 조회에서 사용
	// 전체 공개 북마크, 팔로우 중인 사용자의 일부 공개 북마크, 자신의 북마크 (private 포함) 에 접근 가능하다
	private BooleanBuilder availableByOpenType(long currentUserProfileId) {
		return nullSafeBuilder(() ->
			bookmark.openType.eq(OpenType.ALL)
				.or(bookmark.openType.eq(OpenType.PARTIAL).and(followedBy(currentUserProfileId)))
				.or(bookmark.writer.id.eq(currentUserProfileId))
		);
	}

	private BooleanBuilder registered() {
		return nullSafeBuilder(() -> bookmark.status.eq(BookmarkStatus.REGISTERED));
	}

	// Spring Pageable -> QueryDsl Pageable
	private Pageable convertBookmarkSort(Pageable pageable) {
		return QPageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			toBookmarkOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new)
		);
	}

	private List<OrderSpecifier<?>> toBookmarkOrderSpecifiers(Pageable pageable) {
		final List<OrderSpecifier<?>> result = new ArrayList<>();

		for (Sort.Order order : pageable.getSort()) {
			final String property = order.getProperty();
			if ("like".equals(property)) {
				/* 좋아요 숫자 내림 차순 정렬 */
				result.add(new OrderSpecifier<>(Order.DESC, bookmark.likeCount));
			} else if ("upload".equals(property)) {
				/* 생성일시 내림 차순 정렬 */
				result.add(new OrderSpecifier<>(Order.DESC, bookmark.createdAt));
			}
		}

		/* 생성일시 내림 차순 정렬이 적용되지 않았다면 적용 */
		final boolean containsCreatedAtOrderSpecifier = result.stream()
			.map(OrderSpecifier::getTarget)
			.anyMatch(t -> t.equals(bookmark.createdAt));
		if (!containsCreatedAtOrderSpecifier) {
			result.add(new OrderSpecifier<>(Order.DESC, bookmark.createdAt));
		}

		return result;
	}

	@Override
	public Map<ReactionType, Long> countReactionGroup(final long bookmarkId) {
		/* 리액션 카운트 맵 조회 */
		final Map<ReactionType, Long> reactionCountMap = getJpasqlQuery()
			.select(r_type, count())
			.from(reaction)
			.where(r_bookmarkId.eq(bookmarkId))
			.groupBy(r_type)
			.stream()
			.collect(Collectors.toMap(
				tuple -> (tuple.get(r_type)),
				tuple -> (tuple.get(count())))
			);

		/* 없는 리액션에 대해서 0 채워 주기 */
		Arrays.stream(ReactionType.values())
			.filter(reactionType -> !reactionCountMap.containsKey(reactionType))
			.forEach(reactionType -> reactionCountMap.put(reactionType, 0L));

		return reactionCountMap;
	}
}
