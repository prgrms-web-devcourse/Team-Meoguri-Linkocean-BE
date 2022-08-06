package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.util.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

@Repository
public class CustomBookmarkRepositoryImpl extends Querydsl4RepositorySupport implements CustomBookmarkRepository {

	public CustomBookmarkRepositoryImpl() {
		super(Bookmark.class);
	}

	/* 카테고리로 조회 */
	@Override
	public Page<Bookmark> findByCategory(
		final Category category,
		final BookmarkFindCond cond,
		final Pageable pageable
	) {
		return applyPagination(pageable,
			contentQuery -> contentQuery
				.selectFrom(bookmark)
				.join(bookmark.profile).fetchJoin()
				.join(bookmark.linkMetadata).fetchJoin()
				.where(
					profileIdEq(cond.getProfileId()),
					bookmark.category.eq(category),
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				),
			Bookmark::getTagNames,
			countQuery -> countQuery
				.selectFrom(bookmark)
				.where(
					profileIdEq(cond.getProfileId()),
					bookmark.category.eq(category),
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				)
		);
	}

	/* 즐겨찾기 된 북마크 조회 */
	@Override
	public Page<Bookmark> findFavoriteBookmarks(final BookmarkFindCond cond, final Pageable pageable) {
		return applyPagination(pageable,
			contentQuery -> contentQuery
				.selectFrom(bookmark)
				.join(favorite)
				.on(
					favorite.bookmark.eq(bookmark),
					profileIdEq(cond.getProfileId())
				).where(
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				),
			Bookmark::getTagNames);
	}

	/* 태그로 조회 */
	@Override
	public Page<Bookmark> findByTags(
		final List<String> tagNames,
		final BookmarkFindCond cond,
		final Pageable pageable
	) {
		final List<Long> bookmarkIds =
			select(bookmarkTag.bookmark.id).distinct()
				.from(bookmarkTag)
				.join(bookmarkTag.tag)
				.where(bookmarkTag.tag.name.in(tagNames))
				.fetch();

		return applyPagination(pageable,
			contentQuery -> contentQuery
				.selectFrom(bookmark)
				.join(bookmark.profile).fetchJoin()
				.where(
					bookmark.id.in(bookmarkIds),
					profileIdEq(cond.getProfileId()),
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				),
			Bookmark::getTagNames,
			countQuery -> countQuery
				.selectFrom(bookmark)
				.where(
					bookmark.id.in(bookmarkIds),
					profileIdEq(cond.getProfileId()),
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				)
		);
	}

	/* 기본 조회 */
	@Override
	public Page<Bookmark> findBookmarks(final BookmarkFindCond cond, final Pageable pageable) {

		return applyPagination(pageable,
			contentQuery -> contentQuery
				.selectFrom(bookmark)
				.where(
					profileIdEq(cond.getProfileId()),
					titleContains(cond.getSearchTitle())
					// inOpenTypes(cond.getOpenTypes())
				),
			Bookmark::getTagNames
		);
	}

	private BooleanExpression profileIdEq(final long profileId) {
		return bookmark.profile.id.eq(profileId);
	}

	private BooleanBuilder inOpenTypes(final List<OpenType> openTypes) {
		return nullSafeBuilder(() -> bookmark.openType.in(openTypes));
	}

	private BooleanBuilder titleContains(final String title) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(title));
	}
}
