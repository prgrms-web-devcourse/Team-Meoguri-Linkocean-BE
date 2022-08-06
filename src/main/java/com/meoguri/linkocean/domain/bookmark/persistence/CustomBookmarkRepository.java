package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;

public interface CustomBookmarkRepository {

	/* 카테고리로 조회 */
	Page<Bookmark> findByCategory(Category category, BookmarkFindCond findCond, Pageable pageable);

	/* 즐겨찾기 된 북마크 조회 */
	Page<Bookmark> findFavoriteBookmarks(BookmarkFindCond findCond, Pageable pageable);

	/* 태그로 조회 */
	Page<Bookmark> findByTags(List<String> tags, BookmarkFindCond findCond, Pageable pageable);

	/* 기본 조회 */
	Page<Bookmark> findBookmarks(final BookmarkFindCond findCond, Pageable pageable);
}
