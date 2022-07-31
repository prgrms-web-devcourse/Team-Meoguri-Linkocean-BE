package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark.OpenType;

import lombok.Getter;

/**
 * 피드 북마크 검색 조건 - 쿼리 파라미터의 값을 전달하기 위한 dto
 */
@Getter
public final class FeedBookmarksSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_ORDER = "upload";
	private static final String DEFAULT_OPENTYPE = OpenType.ALL.name();

	private final int page;
	private final int size;
	private final String order;
	private final String category;
	private final String openType;
	private final String searchTitle;

	public FeedBookmarksSearchCond(final Integer page, final Integer size, final String order, final String openType,
		final String category, final String searchTitle) {

		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);
		this.openType = Optional.ofNullable(openType).orElse(DEFAULT_OPENTYPE);

		this.category = category;
		this.searchTitle = searchTitle;
	}
}
