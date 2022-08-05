package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;
import static com.querydsl.core.types.dsl.Expressions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {

	private final JPQLQueryFactory query;

	public CustomBookmarkRepositoryImpl(final EntityManager em) {
		this.query = new JPAQueryFactory(em);
	}

	@Override
	public long countByCategoryAndDefaultCond(final Category category, final FindBookmarksDefaultCond cond) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.where(
				bookmark.profile.id.eq(cond.getProfileId()),
				bookmark.category.eq(category),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByCategoryAndDefaultCond(final Category category,
		final FindBookmarksDefaultCond cond) {

		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.join(bookmark.profile).fetchJoin()
			.join(bookmark.linkMetadata).fetchJoin()
			.where(
				bookmark.profile.id.eq(cond.getProfileId()),
				bookmark.category.eq(category),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).orderBy(getOrderSpecifier(cond.getOrder()))
			.offset(cond.getOffset())
			.limit(cond.getLimit())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByFavoriteAndDefaultCond(final boolean isFavorite, final FindBookmarksDefaultCond cond) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.join(favorite).on(
				favorite.bookmark.eq(bookmark),
				favorite.owner.id.eq(cond.getProfileId()))
			.where(
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes()))
			.fetchOne();
	}

	@Override
	public List<Bookmark> searchByFavoriteAndDefaultCond(final boolean isFavorite,
		final FindBookmarksDefaultCond cond) {

		final List<Bookmark> bookmarks = query
			.select(bookmark)
			.from(bookmark)
			.join(favorite).on(
				favorite.bookmark.eq(bookmark),
				favorite.owner.id.eq(cond.getProfileId()))
			.where(
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes()))
			.orderBy(getOrderSpecifier(cond.getOrder()))
			.offset(cond.getOffset())
			.limit(cond.getLimit())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByTagsAndDefaultCond(final List<String> tagNames, final FindBookmarksDefaultCond cond) {

		final List<Long> bookmarkIds = query
			.select(bookmarkTag.bookmark.id).distinct()
			.from(bookmarkTag)
			.join(bookmarkTag.tag)
			.where(bookmarkTag.tag.name.in(tagNames))
			.fetch();

		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.join(bookmark.profile)
			.where(
				bookmark.id.in(bookmarkIds),
				bookmark.profile.id.eq(cond.getProfileId()),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByTagsAndDefaultCond(final List<String> tagNames, final FindBookmarksDefaultCond cond) {

		final List<Long> bookmarkIds = query
			.select(bookmarkTag.bookmark.id).distinct()
			.from(bookmarkTag)
			.join(bookmarkTag.tag)
			.where(bookmarkTag.tag.name.in(tagNames))
			.fetch();

		final List<Bookmark> bookmarks = query
			.select(bookmark)
			.from(bookmark)
			.join(bookmark.profile).fetchJoin()
			.where(
				bookmark.id.in(bookmarkIds),
				bookmark.profile.id.eq(cond.getProfileId()),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).orderBy(getOrderSpecifier(cond.getOrder()))
			.offset(cond.getOffset())
			.limit(cond.getLimit())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByDefaultCond(final FindBookmarksDefaultCond cond) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.where(
				bookmark.profile.id.eq(cond.getProfileId()),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByDefaultCond(final FindBookmarksDefaultCond cond) {

		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.where(
				bookmark.profile.id.eq(cond.getProfileId()),
				containsSearchTitle(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).orderBy(getOrderSpecifier(cond.getOrder()))
			.offset(cond.getOffset())
			.limit(cond.getLimit())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	private BooleanBuilder inOpenTypes(final List<OpenType> openTypes) {
		return nullSafeBuilder(() -> bookmark.openType.in(openTypes));
	}

	private BooleanBuilder containsSearchTitle(final String searchTitle) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(searchTitle));
	}

	private OrderSpecifier<?>[] getOrderSpecifier(final String order) {
		final List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if (order.equals("like")) {
			orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("bookmark.likeCount")));
		}
		orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("bookmark.updatedAt")));
		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}
}
