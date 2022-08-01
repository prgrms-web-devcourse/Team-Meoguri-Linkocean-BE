package com.meoguri.linkocean.controller.bookmark.dto;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyTagsResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetMyTagsResponse {

	private final String name;
	private final int count;

	public static GetMyTagsResponse ofResult(final GetMyTagsResult result) {

		return new GetMyTagsResponse(result.getTag(), result.getCount());
	}
}
