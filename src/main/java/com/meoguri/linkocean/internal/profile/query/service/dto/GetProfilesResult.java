package com.meoguri.linkocean.internal.profile.query.service.dto;

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
