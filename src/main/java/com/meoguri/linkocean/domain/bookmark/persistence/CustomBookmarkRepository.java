package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;

public interface CustomBookmarkRepository {

	/* 북마크 조회 */
	Page<Bookmark> findByWriterId(BookmarkFindCond findCond, Pageable pageable);
}
