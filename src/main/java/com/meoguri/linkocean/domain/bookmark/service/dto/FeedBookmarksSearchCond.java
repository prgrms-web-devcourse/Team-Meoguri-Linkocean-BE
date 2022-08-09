package com.meoguri.linkocean.domain.bookmark.service.dto;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.Getter;

/**
 * 피드 북마크 검색 조건 - 쿼리 파라미터의 값을 전달하기 위한 dto
 */
@Getter
public final class FeedBookmarksSearchCond {
	private final Category category;
	private final String searchTitle;
	private final boolean follow;

	public FeedBookmarksSearchCond(final Category category, final String searchTitle, final boolean follow) {

		this.category = category;
		this.searchTitle = searchTitle;
		this.follow = follow;
	}
}
