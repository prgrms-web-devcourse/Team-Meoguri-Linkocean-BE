package com.meoguri.linkocean.domain.profile.service.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetProfilesResult {

	private final long profileId;
	private final String username;
	private final String image;
	private final boolean isFollow;
}
