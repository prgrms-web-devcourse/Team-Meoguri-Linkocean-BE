package com.meoguri.linkocean.controller.profile.dto;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetUsedTagWithCountResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetProfileTagsResponse {

	private String tag;
	private long count;

	public static GetProfileTagsResponse of(final GetUsedTagWithCountResult result) {
		return new GetProfileTagsResponse(result.getTag(), result.getCount());
	}
}
