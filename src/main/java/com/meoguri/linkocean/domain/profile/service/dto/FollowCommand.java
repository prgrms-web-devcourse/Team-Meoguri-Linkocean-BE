package com.meoguri.linkocean.domain.profile.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class FollowCommand {

	private final long profileId;
	private final long targetProfileId;
}
