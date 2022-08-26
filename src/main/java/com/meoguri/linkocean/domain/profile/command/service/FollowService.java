package com.meoguri.linkocean.domain.profile.command.service;

public interface FollowService {

	/* 팔로우 */
	void follow(long profileId, long targetProfileId);

	/* 언팔로우 */
	void unfollow(long profileId, long targetProfileId);
}