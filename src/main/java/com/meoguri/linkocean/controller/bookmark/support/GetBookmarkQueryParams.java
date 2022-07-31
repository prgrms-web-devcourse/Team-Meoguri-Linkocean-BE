package com.meoguri.linkocean.controller.bookmark.support;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.BookmarkByUsernameSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final int page;
	private final int size;
	private final String order;
	private final String category;
	private final String searchTitle;

	@Getter
	private final String username;
	private final boolean favorite;
	private final boolean follow;
	private final List<String> tags;

	public MyBookmarkSearchCond toMySearchCond(final Long id) {
		return new MyBookmarkSearchCond(page, size, order, category, searchTitle, tags);
	}

	// TODO - 구현
	public BookmarkByUsernameSearchCond toUsernameSearchCond(final String username) {
		return null;
	}

	public FeedBookmarksSearchCond toFeedSearchCond() {
		return new FeedBookmarksSearchCond(page, size, order, category, searchTitle, follow);
	}
}
