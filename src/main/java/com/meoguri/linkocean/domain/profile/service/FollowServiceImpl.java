package com.meoguri.linkocean.domain.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService {

	private final FollowRepository followRepository;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public void follow(final FollowCommand command) {

		final Profile followerProfile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final Profile followeeProfile = findProfileByIdQuery.findById(command.getTargetProfileId());

		followRepository.save(new Follow(followerProfile, followeeProfile));
	}

	@Override
	public void unfollow(final FollowCommand command) {
		final Profile followerProfile = findProfileByUserIdQuery.findByUserId(command.getUserId());
		final Profile followeeProfile = findProfileByIdQuery.findById(command.getTargetProfileId());

		final Follow follow = followRepository.findByProfiles(followerProfile, followeeProfile)
			.orElseThrow(LinkoceanRuntimeException::new);

		followRepository.delete(follow);
	}

}
