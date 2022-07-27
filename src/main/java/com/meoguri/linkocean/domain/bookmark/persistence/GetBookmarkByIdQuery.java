package com.meoguri.linkocean.domain.bookmark.persistence;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class GetBookmarkByIdQuery {

	private final BookmarkRepository bookmarkRepository;

	public Bookmark GetById(final Long id) {
		return bookmarkRepository.findById(id).orElseThrow(LinkoceanRuntimeException::new);
	}
}