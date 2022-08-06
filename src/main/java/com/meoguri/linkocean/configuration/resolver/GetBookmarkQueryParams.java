package com.meoguri.linkocean.configuration.resolver;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.OtherBookmarkSearchCond;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final int page;
	private final int size;
	private final String order;
	private final String category;
	private final String searchTitle;

	private final boolean favorite;
	private final boolean follow;
	private final List<String> tags;

	public MyBookmarkSearchCond toMySearchCond(final Long id) {
		return new MyBookmarkSearchCond(page, size, order, favorite, category, tags, searchTitle);
	}

	public OtherBookmarkSearchCond toOtherSearchCond(final Long otherProfileId) {
		return new OtherBookmarkSearchCond(otherProfileId, page, size, order, searchTitle, favorite, category, tags);
	}

	public FeedBookmarksSearchCond toFeedSearchCond() {
		return new FeedBookmarksSearchCond(page, size, order, category, searchTitle, follow);
	}
}
