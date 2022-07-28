package com.meoguri.linkocean.domain.profile.service;

import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;

public interface FollowService {

	void follow(FollowCommand command);

	void unfollow(FollowCommand command);
}
