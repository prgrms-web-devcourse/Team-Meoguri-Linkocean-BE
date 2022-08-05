package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;

import lombok.Getter;

/**
 * 내 북마크 검색 조건 - 쿼리 파라미터의 값을 전달하기 위한 dto
 */
@Getter
public final class MyBookmarkSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;
	private static final String DEFAULT_ORDER = "upload";
	private static final String DEFAULT_OPENTYPE = "all";

	private final int page;
	private final int size;
	private final String order;

	private final String category;
	private final boolean favorite;
	private final List<String> tags;

	private final String searchTitle;

	public MyBookmarkSearchCond(final Integer page, final Integer size, final String order,
		final boolean favorite, final String category, final List<String> tags, final String searchTitle) {

		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);

		this.favorite = favorite;
		this.category = category;
		this.tags = tags;

		this.searchTitle = searchTitle;
	}

	public FindBookmarksDefaultCond toFindBookmarksDefaultCond() {
		return new FindBookmarksDefaultCond(this.page, this.size, this.order, this.searchTitle);
	}
}
