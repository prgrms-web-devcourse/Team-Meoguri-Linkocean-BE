package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QBookmarkTag.*;
import static com.meoguri.linkocean.domain.bookmark.entity.QFavorite.*;
import static com.meoguri.linkocean.util.JoinInfoBuilder.Initializer.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.UltimateBookmarkFindCond;
import com.meoguri.linkocean.util.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class CustomBookmarkRepositoryImpl extends Querydsl4RepositorySupport implements CustomBookmarkRepository {

	public CustomBookmarkRepositoryImpl() {
		super(Bookmark.class);
	}

	/* 카테고리로 조회 */
	@Override
	public Page<Bookmark> ultimateFindBookmarks(final UltimateBookmarkFindCond findCond, final Pageable pageable) {

		final long currentUserProfileId = findCond.getCurrentUserProfileId();
		final Long targetProfileId = findCond.getTargetProfileId();
		final Category category = findCond.getCategory();
		final boolean isFavorite = findCond.isFavorite();
		final List<String> tags = findCond.getTags();
		final boolean follow = findCond.isFollow();
		final String title = findCond.getTitle();

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
			pageable,
			base.where(
				categoryEq(category),
				bookmarkIdsIn(bookmarkIds),
				profileIdEq(targetProfileId),
				titleContains(title)
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

	private BooleanBuilder inOpenTypes(final List<OpenType> openTypes) {
		return nullSafeBuilder(() -> bookmark.openType.in(openTypes));
	}

	private BooleanBuilder titleContains(final String title) {
		return nullSafeBuilder(() -> bookmark.title.containsIgnoreCase(title));
	}

	private BooleanBuilder bookmarkIdsIn(final List<Long> bookmarkIds) {
		return nullSafeBuilder(() -> bookmark.id.in(bookmarkIds));
	}

}
