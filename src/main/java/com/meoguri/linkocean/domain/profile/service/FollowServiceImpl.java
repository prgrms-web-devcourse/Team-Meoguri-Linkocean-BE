package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.exception.Preconditions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService {

	private final FollowRepository followRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public void follow(final FollowCommand command) {
		final Profile follower = findProfileByIdQuery.findById(command.getProfileId());
		final Profile followee = findProfileByIdQuery.findById(command.getTargetProfileId());

		followRepository.save(new Follow(follower, followee));
	}

	@Override
	public void unfollow(final FollowCommand command) {
		final long followerId = command.getProfileId();
		final long followeeId = command.getTargetProfileId();

		long count = followRepository.deleteByFollower_idAndFollowee_id(followerId, followeeId);

		checkCondition(count == 1,
			"illegal unfollow command of profileId " + followeeId + " on " + "targetProfileId" + followeeId);
	}
}
