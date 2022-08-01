package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.Optional;

import lombok.Getter;

/**
 * 피드 북마크 검색 조건 - 쿼리 파라미터의 값을 전달하기 위한 dto
 */
@Getter
public final class FeedBookmarksSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_ORDER = "upload";

	private final int page;
	private final int size;
	private final String order;
	private final String category;
	private final String searchTitle;
	private final boolean follow;

	public FeedBookmarksSearchCond(final Integer page, final Integer size, final String order, final String category,
		final String searchTitle, final boolean follow) {

		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);

		this.category = category;
		this.searchTitle = searchTitle;
		this.follow = follow;
	}
}
