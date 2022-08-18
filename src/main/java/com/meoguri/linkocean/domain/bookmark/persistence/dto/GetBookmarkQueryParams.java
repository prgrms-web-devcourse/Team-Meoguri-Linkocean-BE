package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final Category category;
	private final String searchTitle;
	private final Boolean favorite;
	private final Boolean follow;
	private final List<String> tags;
}
