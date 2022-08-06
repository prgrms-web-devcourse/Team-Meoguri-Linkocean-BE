package com.meoguri.linkocean.configuration.resolver;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.OtherBookmarkSearchCond;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
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

	public MyBookmarkSearchCond toMySearchCond(final long userId) {
		return new MyBookmarkSearchCond(userId, favorite, category, tags, searchTitle, order);
	}

	public OtherBookmarkSearchCond toOtherSearchCond(final Long otherProfileId) {
		return new OtherBookmarkSearchCond(otherProfileId, page, size, order, searchTitle, favorite, category, tags);
	}

	public FeedBookmarksSearchCond toFeedSearchCond() {
		return new FeedBookmarksSearchCond(page, size, order, category, searchTitle, follow);
	}

	public Pageable toPage() {
		return PageRequest.of(page, size, Sort.DEFAULT_DIRECTION);
	}
}
