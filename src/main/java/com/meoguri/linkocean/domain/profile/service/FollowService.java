package com.meoguri.linkocean.domain.profile.service;

import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;

public interface FollowService {

	/* 팔로우 */
	void follow(FollowCommand command);

	/* 언팔로우 */
	void unfollow(FollowCommand command);
}
