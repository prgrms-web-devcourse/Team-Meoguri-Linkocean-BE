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
import com.meoguri.linkocean.domain.profile.entity.Profile;
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
	public long countByCategory(final Profile profile, final Category category, final String searchTitle) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.where(
				bookmark.profile.eq(profile),
				bookmark.category.eq(category),
				filterBySearchTitle(searchTitle)
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByCategory(final Profile profile, final Category category, final String searchTitle,
		final String order, final int page, final int size) {

		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.join(bookmark.profile).fetchJoin()
			.join(bookmark.linkMetadata).fetchJoin()
			.where(
				bookmark.profile.eq(profile),
				bookmark.category.eq(category),
				filterBySearchTitle(searchTitle)
			).orderBy(getOrderSpecifier(order))
			.offset((page - 1) * size)
			.limit(size)
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByFavorite(final Profile profile, final boolean isFavorite, final String searchTitle) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.join(favorite).on(
				favorite.bookmark.eq(bookmark),
				favorite.owner.eq(profile))
			.where(filterBySearchTitle(searchTitle))
			.fetchOne();
	}

	@Override
	public List<Bookmark> searchByFavorite(final Profile profile, final boolean isFavorite, final String searchTitle,
		final String order, final int page, final int size) {

		final List<Bookmark> bookmarks = query
			.select(bookmark)
			.from(bookmark)
			.join(favorite).on(
				favorite.bookmark.eq(bookmark),
				favorite.owner.eq(profile))
			.where(filterBySearchTitle(searchTitle))
			.orderBy(getOrderSpecifier(order))
			.offset((page - 1) * size)
			.limit(size)
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByTags(final Profile profile, final List<String> tagNames, final String searchTitle) {

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
				bookmark.profile.eq(profile),
				filterBySearchTitle(searchTitle)
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByTags(final Profile profile, final List<String> tagNames, final String searchTitle,
		final String order, final int page, final int size) {

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
				bookmark.profile.eq(profile),
				filterBySearchTitle(searchTitle)
			).orderBy(getOrderSpecifier(order))
			.offset((page - 1) * size)
			.limit(size)
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	@Override
	public long countByProfile(final Profile profile, final String searchTitle) {
		return query
			.select(bookmark.id.count())
			.from(bookmark)
			.where(
				bookmark.profile.eq(profile),
				filterBySearchTitle(searchTitle)
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByProfile(final Profile profile, final String searchTitle, final String order,
		final int page, final int size) {

		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.where(
				bookmark.profile.eq(profile),
				filterBySearchTitle(searchTitle)
			).orderBy(getOrderSpecifier(order))
			.offset((page - 1) * size)
			.limit(size)
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	private BooleanBuilder filterBySearchTitle(final String searchTitle) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(searchTitle));
	}

	private OrderSpecifier<?>[] getOrderSpecifier(final String order) {
		final List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if (order.equals("like")) {
			orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("likeCount")));
		}
		orderSpecifiers.add(new OrderSpecifier(Order.DESC, stringPath("bookmark.updatedAt")));
		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}
}
