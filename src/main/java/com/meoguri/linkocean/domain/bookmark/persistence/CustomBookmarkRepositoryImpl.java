package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;
import com.meoguri.linkocean.domain.profile.entity.QProfile;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {

	private final JPQLQueryFactory query;

	public CustomBookmarkRepositoryImpl(final EntityManager em) {
		this.query = new JPAQueryFactory(em);
	}

	/* 카테고리로 조회 */
	@Override
	public Page<Bookmark> findByCategory(
		final Category category,
		final FindBookmarksDefaultCond cond,
		final Pageable pageable
	) {
		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.join(bookmark.profile).fetchJoin()
			.join(bookmark.linkMetadata).fetchJoin()
			.where(
				profileIdEq(bookmark.profile, cond),
				bookmark.category.eq(category),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);

		final long count = countByCategory(category, cond);
		return new PageImpl<>(bookmarks, pageable, count);
	}

	private long countByCategory(final Category category, final FindBookmarksDefaultCond cond) {
		return query
			.select(bookmark.count())
			.from(bookmark)
			.where(
				profileIdEq(bookmark.profile, cond),
				bookmark.category.eq(category),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	/* 즐겨찾기 된 북마크 조회 */
	@Override
	public Page<Bookmark> findFavoriteBookmarks(final FindBookmarksDefaultCond cond, final Pageable pageable) {

		final List<Bookmark> bookmarks =
			query
				.select(bookmark)
				.from(bookmark)
				.join(favorite)
				.on(
					favorite.bookmark.eq(bookmark),
					profileIdEq(favorite.owner, cond))
				.where(
					titleLike(cond.getSearchTitle()),
					inOpenTypes(cond.getOpenTypes())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		final long count = countFavoriteBookmarks(cond);

		return new PageImpl<>(bookmarks, pageable, count);
	}

	private long countFavoriteBookmarks(final FindBookmarksDefaultCond cond) {
		return query
			.select(bookmark.count())
			.from(bookmark)
			.join(favorite).on(
				favorite.bookmark.eq(bookmark),
				profileIdEq(favorite.owner, cond)
			)
			.where(
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			)
			.fetchOne();
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
				profileIdEq(bookmark.profile, cond),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByTagsAndDefaultCond(final List<String> tagNames, final FindBookmarksDefaultCond cond,
		final
		Pageable pageable) {

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
				profileIdEq(bookmark.profile, cond),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
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
				profileIdEq(bookmark.profile, cond),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			).fetchOne();
	}

	@Override
	public List<Bookmark> searchByDefaultCond(final FindBookmarksDefaultCond cond, final Pageable pageable) {

		final List<Bookmark> bookmarks = query
			.selectFrom(bookmark)
			.where(
				profileIdEq(bookmark.profile, cond),
				titleLike(cond.getSearchTitle()),
				inOpenTypes(cond.getOpenTypes())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// Lazy Loading (배치 옵션 이용)
		bookmarks.forEach(Bookmark::getTagNames);
		return bookmarks;
	}

	private BooleanExpression profileIdEq(final QProfile bookmark, final FindBookmarksDefaultCond cond) {
		return bookmark.id.eq(cond.getProfileId());
	}

	private BooleanBuilder inOpenTypes(final List<OpenType> openTypes) {
		return nullSafeBuilder(() -> bookmark.openType.in(openTypes));
	}

	private BooleanBuilder titleLike(final String title) {
		return nullSafeBuilder(() -> bookmark.title.like(String.join(title, "%", "%")));
	}

}
