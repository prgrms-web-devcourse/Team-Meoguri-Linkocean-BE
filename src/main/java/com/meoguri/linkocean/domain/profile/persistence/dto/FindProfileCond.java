package com.meoguri.linkocean.domain.profile.persistence.dto;

import lombok.Getter;

@Getter
public final class FindProfileCond {

	private final Long profileId;
	private final int offset;
	private final int limit;
	private final String username;

	public FindProfileCond(final Long profileId, final int page, final int size, final String username) {
		this.profileId = profileId;
		this.offset = (page - 1) * size;
		this.limit = size;
		this.username = username;
	}

	public FindProfileCond(final int page, final int size, final String username) {
		this(null, page, size, username);
	}
}
