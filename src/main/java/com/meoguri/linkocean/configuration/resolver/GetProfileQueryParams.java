package com.meoguri.linkocean.configuration.resolver;

import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GetProfileQueryParams {

	private final int page;
	private final int size;
	private final String username;

	public ProfileSearchCond toSearchCond(final long profileId) {
		return new ProfileSearchCond(profileId, page, size, username);
	}
}
