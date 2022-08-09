package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;

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

	public List<Boolean> isFollow(final long followerId, final List<Profile> followees) {
		return null;
	}

}
