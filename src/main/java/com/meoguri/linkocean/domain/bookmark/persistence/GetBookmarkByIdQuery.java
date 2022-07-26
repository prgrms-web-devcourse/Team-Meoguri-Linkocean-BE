package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetBookmarkByIdQuery {

	private final BookmarkRepository bookmarkRepository;

	public Bookmark GetById(final Long id) {
		return bookmarkRepository.findById(id).orElseThrow(LinkoceanRuntimeException::new);
	}
}
