package com.meoguri.linkocean.internal.bookmark.persistence.dto;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final String category;
	private final String searchTitle;
	private final Boolean favorite;
	private final Boolean follow;
	private final List<String> tags;

	public Category getCategory() {
		return Category.of(category);
	}
}
