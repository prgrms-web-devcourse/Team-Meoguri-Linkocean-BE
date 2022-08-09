package com.meoguri.linkocean.domain.profile.persistence;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class CheckIsFollowQuery {

	private final FollowRepository followRepository;

	public boolean isFollow(final long followerId, final Profile followee) {
		return followRepository.existsByFollower_idAndFollowee(followerId, followee);
	}

}
