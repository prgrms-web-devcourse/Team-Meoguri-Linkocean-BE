package com.meoguri.linkocean.controller.bookmark.dto;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyTagsResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class MyTagsResponse {

	private final String name;
	private final int count;

	public static MyTagsResponse ofResult(final GetMyTagsResult result) {

		return new MyTagsResponse(result.getTag(), result.getCount());
	}
}
