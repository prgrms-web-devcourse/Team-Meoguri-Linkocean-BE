package com.meoguri.linkocean.domain.bookmark.persistence;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class UpdateBookmarkLikeCountQuery {

	private final BookmarkRepository bookmarkRepository;

	public int updateBookmarkLikeCountById(final Long likeCount, final Long bookmarkId) {
		return bookmarkRepository.updateBookmarkLikeCount(likeCount, bookmarkId);
	}
}
