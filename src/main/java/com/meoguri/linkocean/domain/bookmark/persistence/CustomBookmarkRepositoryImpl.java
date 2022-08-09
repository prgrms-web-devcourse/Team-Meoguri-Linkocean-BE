package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.util.JoinInfoBuilder.Initializer.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.util.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class CustomBookmarkRepositoryImpl extends Querydsl4RepositorySupport implements CustomBookmarkRepository {

	public CustomBookmarkRepositoryImpl() {
		super(Bookmark.class);
	}

	/* 카테고리로 조회 */
	@Override
	public Page<Bookmark> findByWriterId(final BookmarkFindCond findCond, final Pageable pageable) {

		final long currentUserProfileId = findCond.getCurrentUserProfileId();
		final Long targetProfileId = findCond.getTargetProfileId();
		final Category category = findCond.getCategory();
		final boolean isFavorite = findCond.isFavorite();
		final List<String> tags = findCond.getTags();
		final boolean follow = findCond.isFollow();
		final String title = findCond.getTitle();
		final OpenType openType = findCond.getOpenType();

		JPAQuery<Bookmark> base = selectFrom(bookmark);

		joinIf(category != null, base,
			() -> join(bookmark.profile).fetchJoin()
				.join(bookmark.linkMetadata).fetchJoin());

		joinIf(isFavorite, base,
			() -> join(favorite)
				.on(favorite.bookmark.eq(bookmark),
					profileIdEq(targetProfileId)));

		joinIf(tags != null, base,
			() -> join(bookmark.profile).fetchJoin());

		final List<Long> bookmarkIds = getBookmarkIds(tags);
		return applyPagination(
			convertBookmarkSort(pageable),
			base.where(
				categoryEq(category),
				bookmarkIdsIn(bookmarkIds),
				profileIdEq(targetProfileId),
				titleContains(title),
				bookmark.status.eq(BookmarkStatus.REGISTERED),
				availableOpenType(openType)
			),
			Bookmark::getTagNames
		);
	}

	private List<Long> getBookmarkIds(final List<String> tags) {
		return tags != null ? select(bookmarkTag.bookmark.id)
			.distinct()
			.from(bookmarkTag)
			.join(bookmarkTag.tag)
			.where(bookmarkTag.tag.name.in(tags))
			.fetch() : null;
	}

	private BooleanBuilder profileIdEq(final long profileId) {
		return nullSafeBuilder(() -> bookmark.profile.id.eq(profileId));
	}

	private BooleanBuilder categoryEq(final Category category) {
		return nullSafeBuilder(() -> bookmark.category.eq(category));
	}

	private BooleanBuilder titleContains(final String title) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(title));
	}

	private BooleanBuilder bookmarkIdsIn(final List<Long> bookmarkIds) {
		return nullSafeBuilder(() -> bookmark.id.in(bookmarkIds));
	}

	private BooleanBuilder availableOpenType(final OpenType openType) {
		// PRIVATE 이상을 조회 하는 요청이므로 필터링이 필요 없음
		if (openType == OpenType.PRIVATE) {
			return new BooleanBuilder();
		}

		// 주어진 openType 이하의 모든 openType 을 조회 할 필요가 있음
		return nullSafeBuilder(() -> bookmark.openType.loe(openType));
	}

	private Pageable convertBookmarkSort(Pageable pageable) {
		return QPageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			toBookmarkOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new)
		);
	}

	private List<OrderSpecifier<?>> toBookmarkOrderSpecifiers(Pageable pageable) {
		final Order direction = Order.DESC;
		final List<OrderSpecifier<?>> result = new ArrayList<>();

		for (Sort.Order order : pageable.getSort()) {
			switch (order.getProperty()) {
				case "like":
					result.add(new OrderSpecifier<>(direction, bookmark.likeCount));
					break;
				case "upload":
					result.add(new OrderSpecifier<>(direction, bookmark.createdAt));
					break;
			}
		}
		return result;
	}
}
