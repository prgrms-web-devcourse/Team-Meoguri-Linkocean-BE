package com.meoguri.linkocean.controller.profile.support;

import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GetProfileQueryParams {

	private final int page;
	private final int size;
	private final String username;

	public ProfileSearchCond toSearchCond(final long userId) {
		return new ProfileSearchCond(userId, page, size, username);
	}
}
