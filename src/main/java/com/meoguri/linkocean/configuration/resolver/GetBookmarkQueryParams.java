package com.meoguri.linkocean.configuration.resolver;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final Category category;
	private final String title;
	private final boolean favorite;
	private final boolean follow;
	private final List<String> tags;
}
