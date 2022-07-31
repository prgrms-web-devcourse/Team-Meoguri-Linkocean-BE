package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 내 북마크 검색 조건 - 쿼리 파라미터의 값을 전달하기 위한 dto
 */
@Getter
@RequiredArgsConstructor
public final class MyBookmarkSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_ORDER = "upload";
	private static final String DEFAULT_OPENTYPE = Bookmark.OpenType.ALL.name();

	private final int page;
	private final int size;
	private final String order;
	private final String category;
	private final String searchTitle;
	private final List<String> tags;

	public MyBookmarkSearchCond(final Integer page, final Integer size, final String order,
		final String category, final String searchTitle, final List<String> tags) {

		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.order = Optional.ofNullable(order).orElse(DEFAULT_ORDER);

		this.category = category;
		this.searchTitle = searchTitle;
		this.tags = tags;
	}
}
