package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QReaction.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;
import static com.querydsl.core.types.dsl.Expressions.*;
import static com.querydsl.jpa.JPAExpressions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkQueryDto;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkTagQueryDto;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {

	private final JPQLQueryFactory query;

	public CustomBookmarkRepositoryImpl(final EntityManager em) {
		this.query = new JPAQueryFactory(em);
	}

	@Override
	public List<BookmarkQueryDto> findMyBookmarksUsingSearchCond(final Profile profile,
		final MyBookmarkSearchCond searchCond) {

		int offset = (searchCond.getPage() - 1) * searchCond.getSize();

		/* ToOne 관계의 데이터는 같이 가져오기 & 필터링 (테그는 ToMany 관계라 나중에 가져오고 필터링 진행) */
		final List<BookmarkQueryDto> result = query
			.select(Projections.fields(BookmarkQueryDto.class,
				bookmark.id,
				bookmark.linkMetadata.url.url,
				bookmark.title,
				bookmark.openType,
				bookmark.category,
				bookmark.updatedAt,
				ExpressionUtils.as(
					select(favorite.count()
						.when(0L).then(false)
						.otherwise(true))
						.from(favorite)
						.where(favorite.bookmark.eq(bookmark)
							.and(favorite.owner.eq(profile))), "isFavorite"),
				ExpressionUtils.as(
					select(reaction.count())
						.from(reaction)
						.where(reaction.bookmark.eq(bookmark)
							.and(reaction.type.eq(Reaction.ReactionType.LIKE))), "likeCount"),
				bookmark.linkMetadata.imageUrl))
			.from(bookmark)
			.join(bookmark.linkMetadata)
			.where(
				filterByCategory(searchCond.getCategory()),
				filterBySearchTitle(searchCond.getSearchTitle())
			)
			.orderBy(getOrderSpecifier(searchCond.getOrder()))
			.offset(offset)
			.limit(searchCond.getSize())
			.fetch();

		/* 북마크 테그 한번에 가져오기 */
		Map<Long, List<BookmarkTagQueryDto>> bookmarkTagMap = findBookmarkTagMap(toBookmarkId(result));

		/* 가져온 북마크 테그 BookmarkQueryDto에 세팅하기 */
		result.forEach(o -> o.setTagNames(bookmarkTagMap.getOrDefault(o.getId(), Collections.emptyList())));

		/* 테그로 필터링 후 반환 */
		return result.stream()
			.filter(r -> filterByTags(r, searchCond.getTags()))
			.collect(Collectors.toList());
	}

	private BooleanBuilder filterByCategory(final String category) {
		return nullSafeBuilder(() -> bookmark.category.eq(Category.of(category)));
	}

	private BooleanBuilder filterBySearchTitle(final String searchTitle) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(searchTitle));
	}

	/**
	 * 기본 정렬은 최신순
	 * 만약 좋아요 정렬 조건이 입력으로 들어오면 좋아요 순으로 정렬 후 최신순 정렬
	 */
	private OrderSpecifier<?>[] getOrderSpecifier(final String order) {
		final List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if (order.equals("like")) {
			orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("likeCount")));
		}
		orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("bookmark.updatedAt")));
		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}

	private List<Long> toBookmarkId(final List<BookmarkQueryDto> result) {
		return result.stream()
			.map(BookmarkQueryDto::getId)
			.collect(Collectors.toList());
	}

	private Map<Long, List<BookmarkTagQueryDto>> findBookmarkTagMap(final List<Long> bookmarkIds) {
		List<BookmarkTagQueryDto> bookmarkTags = query
			.select(Projections.constructor(BookmarkTagQueryDto.class,
				bookmarkTag.bookmark.id.as("bookmarkId"),
				bookmarkTag.tag.name.as("tagName")))
			.from(bookmarkTag)
			.join(bookmarkTag.tag)
			.where(bookmarkTag.bookmark.id.in(bookmarkIds))
			.fetch();

		return bookmarkTags.stream()
			.collect(Collectors.groupingBy(BookmarkTagQueryDto::getBookmarkId));
	}

	private boolean filterByTags(final BookmarkQueryDto bookmark, final List<String> tags) {
		return Objects.isNull(tags) || bookmark.getTagNames().stream().anyMatch(tags::contains);
	}
}
