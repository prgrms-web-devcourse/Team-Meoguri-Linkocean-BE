package com.meoguri.linkocean.controller.bookmark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetDuplicatedUrlResponse {
	private final Boolean isDuplicateUrl;

	public static GetDuplicatedUrlResponse of(final boolean isDuplicateUrl) {
		return new GetDuplicatedUrlResponse(isDuplicateUrl);
	}
}
