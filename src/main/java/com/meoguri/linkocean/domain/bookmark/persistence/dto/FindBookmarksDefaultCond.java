package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindBookmarksDefaultCond {
	private final int page;
	private final int size;
	private final String order;
	private final String searchTitle;
}
