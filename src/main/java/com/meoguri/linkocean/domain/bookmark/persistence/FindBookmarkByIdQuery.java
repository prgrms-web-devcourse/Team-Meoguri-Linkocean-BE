package com.meoguri.linkocean.domain.bookmark.persistence;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.support.domain.persistence.Query;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindBookmarkByIdQuery {

	private final BookmarkRepository bookmarkRepository;

	public Bookmark findById(final Long id) {
		return bookmarkRepository.findBookmarkById(id, bookmarkRepository::findById);
	}

	public Bookmark findByIdFetchReactions(final long id) {
		return bookmarkRepository.findBookmarkById(id, bookmarkRepository::findByIdFetchReactions);
	}
}
