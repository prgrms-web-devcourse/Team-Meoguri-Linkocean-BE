package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;

// TODO - 구현
public final class OtherBookmarkSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;
	private static final String DEFAULT_ORDER = "upload";

	private final long profileId;
	private final int page;
	private final int size;
	private final String order;
	private final String searchTitle;

	private final String category;
	private final boolean favorite;
	private final List<String> tags;

	public OtherBookmarkSearchCond(final Long profileId, final Integer page, final Integer size, final String order,
		final String searchTitle, final boolean favorite, final String category, final List<String> tags) {

		this.profileId = profileId;
		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);
		this.searchTitle = searchTitle;

		this.favorite = favorite;
		this.category = category;
		this.tags = tags;
	}

	public FindBookmarksDefaultCond toFindBookmarksDefaultCond() {
		return new FindBookmarksDefaultCond(this.page, this.size, this.order, this.searchTitle);
	}
}
