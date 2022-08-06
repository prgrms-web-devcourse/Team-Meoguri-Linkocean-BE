package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;

public interface CustomBookmarkRepository {

	long countByCategoryAndDefaultCond(final Category category, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByCategoryAndDefaultCond(final Category category, final FindBookmarksDefaultCond cond, final
	Pageable pageable);

	long countByFavoriteAndDefaultCond(final boolean favorite, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByFavoriteAndDefaultCond(boolean favorite, final FindBookmarksDefaultCond cond, final
	Pageable pageable);

	long countByTagsAndDefaultCond(final List<String> tags, final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByTagsAndDefaultCond(final List<String> tags, final FindBookmarksDefaultCond cond, final
	Pageable pageable);

	long countByDefaultCond(final FindBookmarksDefaultCond cond);

	List<Bookmark> searchByDefaultCond(final FindBookmarksDefaultCond cond, final
	Pageable pageable);
}
