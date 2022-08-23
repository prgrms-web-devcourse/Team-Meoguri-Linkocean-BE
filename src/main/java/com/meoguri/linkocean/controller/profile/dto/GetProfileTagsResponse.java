package com.meoguri.linkocean.controller.profile.dto;

import com.meoguri.linkocean.domain.profile.service.command.dto.GetProfileTagsResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetProfileTagsResponse {

	private String tag;
	private int count;

	public static GetProfileTagsResponse of(final GetProfileTagsResult result) {
		return new GetProfileTagsResponse(result.getTag(), result.getCount());
	}
}
