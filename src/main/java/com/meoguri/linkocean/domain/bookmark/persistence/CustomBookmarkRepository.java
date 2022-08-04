package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface CustomBookmarkRepository {

	long countByProfileAndCategoryAndDefaultCond(final Profile profile, final Category of, final String searchTitle);

	List<Bookmark> searchByProfileAndCategoryAndDefaultCond(final Profile profile, final Category of,
		final FindBookmarksDefaultCond cond);

	long countByProfileAndFavoriteAndDefaultCond(final Profile profile, final boolean favorite,
		final String searchTitle);

	List<Bookmark> searchByProfileAndFavoriteAndDefaultCond(final Profile profile, boolean favorite,
		final FindBookmarksDefaultCond cond);

	long countByProfileAndTagsAndDefaultCond(final Profile profile, final List<String> tags, final String searchTitle);

	List<Bookmark> searchByProfileAndTagsAndDefaultCond(final Profile profile, final List<String> tags,
		final FindBookmarksDefaultCond cond);

	long countByProfileAndDefaultCond(final Profile profile, final String searchTitle);

	List<Bookmark> searchByProfileAndDefaultCond(final Profile profile, final FindBookmarksDefaultCond cond);
}
