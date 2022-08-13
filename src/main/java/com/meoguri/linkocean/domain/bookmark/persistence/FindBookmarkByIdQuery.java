package com.meoguri.linkocean.domain.bookmark.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindBookmarkByIdQuery {

	private final BookmarkRepository bookmarkRepository;

	public Bookmark findById(final Long id) {
		return bookmarkRepository.findById(id)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such bookmark id %d", id)));
	}
}
