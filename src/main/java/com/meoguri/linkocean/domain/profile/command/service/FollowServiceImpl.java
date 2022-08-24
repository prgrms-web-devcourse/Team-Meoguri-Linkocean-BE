package com.meoguri.linkocean.domain.profile.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.profile.command.persistence.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService {

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public void follow(final long profileId, final long targetProfileId) {
		final Profile profile = findProfileByIdQuery.findProfileFetchFollows(profileId);
		final Profile target = findProfileByIdQuery.findById(targetProfileId);

		profile.follow(target);
	}

	@Override
	public void unfollow(final long profileId, final long targetProfileId) {
		final Profile profile = findProfileByIdQuery.findProfileFetchFollows(profileId);
		final Profile target = findProfileByIdQuery.findById(targetProfileId);

		profile.unfollow(target);
	}
}
