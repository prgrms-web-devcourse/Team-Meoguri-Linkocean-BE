package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;

import lombok.Getter;

/**
 * 내 북마크 검색 조건
 */
@Getter
public final class MyBookmarkSearchCond {

	private final long profileId;
	private final String category;
	private final boolean favorite;
	private final List<String> tags;

	private final String searchTitle;

	public MyBookmarkSearchCond(final long profileId, final boolean favorite, final String category,
		final List<String> tags, final String searchTitle) {

		this.profileId = profileId;
		this.favorite = favorite;
		this.category = category;
		this.tags = tags;
		this.searchTitle = searchTitle;
	}

	public BookmarkFindCond toFindCond(final long profileId) {
		return new BookmarkFindCond(profileId, searchTitle);
	}
}
