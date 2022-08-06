package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;

public interface CustomBookmarkRepository {

	long countByCategoryAndDefaultCond(final Category category, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByCategoryAndDefaultCond(final Category of, final FindBookmarksDefaultCond cond);

	long countByFavoriteAndDefaultCond(final boolean favorite, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByFavoriteAndDefaultCond(boolean favorite, final FindBookmarksDefaultCond cond);

	long countByTagsAndDefaultCond(final List<String> tags, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByTagsAndDefaultCond(final List<String> tags, final FindBookmarksDefaultCond cond);

	long countByDefaultCond(final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByDefaultCond(final FindBookmarksDefaultCond cond);
}
