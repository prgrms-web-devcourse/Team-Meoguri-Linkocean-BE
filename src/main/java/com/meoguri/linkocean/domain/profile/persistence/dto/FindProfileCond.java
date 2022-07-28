package com.meoguri.linkocean.domain.profile.persistence.dto;

import lombok.Getter;

@Getter
public class FindProfileCond {

	private final long profileId;
	private final int offset;
	private final int limit;
	private final String username;

	public FindProfileCond(final long profileId, final int page, final int size, final String username) {
		this.profileId = profileId;
		this.offset = (page - 1) * size;
		this.limit = size;
		this.username = username;
	}
}
