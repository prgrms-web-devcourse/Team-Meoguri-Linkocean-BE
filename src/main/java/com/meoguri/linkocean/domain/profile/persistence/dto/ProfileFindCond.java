package com.meoguri.linkocean.domain.profile.persistence.dto;

import lombok.Getter;

/* 프로필 조회 조건 */
@Getter
public final class ProfileFindCond {

	private final Long profileId;
	private final int offset;
	private final int limit;
	private final String username;

	public ProfileFindCond(final Long profileId, final int page, final int size, final String username) {
		this.profileId = profileId;
		this.offset = (page - 1) * size;
		this.limit = size;
		this.username = username;
	}

	public ProfileFindCond(final int page, final int size, final String username) {
		this(null, page, size, username);
	}
}
