package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.lang.String.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService {

	private final FollowRepository followRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public void follow(final long profileId, final long targetProfileId) {
		final Profile follower = findProfileByIdQuery.findById(profileId);
		final Profile followee = findProfileByIdQuery.findById(targetProfileId);

		final Optional<Follow> oFollow = followRepository.findByProfiles(follower, followee);
		checkUniqueConstraint(oFollow,
			format("illegal unfollow command of profileId: %d on targetProfileId: %d", profileId, targetProfileId));

		followRepository.save(new Follow(follower, followee));
	}

	@Override
	public void unfollow(final long profileId, final long targetProfileId) {
		long count = followRepository.deleteByFollower_idAndFollowee_id(profileId, targetProfileId);

		checkCondition(count == 1,
			format("illegal unfollow command of profileId: %d on targetProfileId: %d", profileId, targetProfileId));
	}
}
