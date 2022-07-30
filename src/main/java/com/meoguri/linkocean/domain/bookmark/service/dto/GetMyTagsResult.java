package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetMyTagsResult {

	private final String tag;
	private final int count;
}
