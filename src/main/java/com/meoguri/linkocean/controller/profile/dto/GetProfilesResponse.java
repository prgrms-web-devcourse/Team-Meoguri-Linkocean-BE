package com.meoguri.linkocean.controller.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetProfilesResponse {

	private long id;
	private String username;

	@JsonProperty("imageUrl")
	private String image;
	private Boolean isFollow;

	public static GetProfilesResponse of(final SearchProfileResult result) {
		return new GetProfilesResponse(
			result.getId(),
			result.getUsername(),
			result.getImage(),
			result.isFollow()
		);
	}
}
