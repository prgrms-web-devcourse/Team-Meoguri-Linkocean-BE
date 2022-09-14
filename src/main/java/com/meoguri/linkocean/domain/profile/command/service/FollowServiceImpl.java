package com.meoguri.linkocean.domain.profile.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.FindProfileByIdRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService {

	private final FindProfileByIdRepository findProfileByIdRepository;

	@Override
	public void follow(final long profileId, final long targetProfileId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFollows(profileId);
		final Profile target = findProfileByIdRepository.findById(targetProfileId);

		profile.follow(target);
	}

	@Override
	public void unfollow(final long profileId, final long targetProfileId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFollows(profileId);
		final Profile target = findProfileByIdRepository.findById(targetProfileId);

		profile.unfollow(target);
	}
}
