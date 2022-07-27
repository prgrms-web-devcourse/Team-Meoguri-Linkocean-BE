package com.meoguri.linkocean.domain.profile.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowCommand {

	private final long userId;
	private final long targetProfileId;
}
