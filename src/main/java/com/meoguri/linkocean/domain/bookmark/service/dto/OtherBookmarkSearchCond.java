package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;

import lombok.Getter;

@Getter
public final class OtherBookmarkSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;
	private static final String DEFAULT_ORDER = "upload";

	private final long otherProfileId;
	private final int page;
	private final int size;
	private final String order;
	private final String searchTitle;

	private final String category;
	private final boolean favorite;
	private final List<String> tags;

	public OtherBookmarkSearchCond(final Long otherProfileId, final Integer page, final Integer size,
		final String order,
		final String searchTitle, final boolean favorite, final String category, final List<String> tags) {

		this.otherProfileId = otherProfileId;
		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);
		this.searchTitle = searchTitle;

		this.favorite = favorite;
		this.category = category;
		this.tags = tags;
	}

	public FindBookmarksDefaultCond toFindBookmarksDefaultCond() {
		return new FindBookmarksDefaultCond(this.page, this.size, this.order, this.otherProfileId, this.searchTitle);
	}
}
