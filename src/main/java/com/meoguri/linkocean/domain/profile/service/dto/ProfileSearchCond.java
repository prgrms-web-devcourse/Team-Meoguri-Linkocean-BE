package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.Optional;

import lombok.Getter;

@Getter
public final class ProfileSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;

	private final long profileId;
	private final int page;
	private final int size;
	private final String username;

	public ProfileSearchCond(final long profileId, final Integer page, final Integer size, final String username) {

		this.profileId = profileId;
		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.username = username;
	}
}
