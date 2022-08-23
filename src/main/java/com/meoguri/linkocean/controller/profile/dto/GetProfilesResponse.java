package com.meoguri.linkocean.controller.profile.dto;

import com.meoguri.linkocean.domain.profile.service.command.dto.GetProfilesResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetProfilesResponse {

	private long profileId;
	private String username;
	private String imageUrl;
	private Boolean isFollow;

	public static GetProfilesResponse of(final GetProfilesResult result) {
		return new GetProfilesResponse(
			result.getProfileId(),
			result.getUsername(),
			result.getImage(),
			result.isFollow()
		);
	}
}
