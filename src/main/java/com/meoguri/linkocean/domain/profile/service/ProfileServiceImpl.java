package com.meoguri.linkocean.domain.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByIdQuery;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ProfileServiceImpl implements ProfileService {

	private final FindUserByIdQuery findUserByIdQuery;
	private final ProfileRepository profileRepository;

	@Override
	public void registerProfile(final RegisterProfileCommand command) {

		final User user = findUserByIdQuery.findById(command.getUserId());

		final Profile profile = new Profile(user, command.getUsername());
		profileRepository.save(profile);

		//TODO - 선호 카테고리 등록 : 처리 어떻게?
	}

	@Transactional(readOnly = true)
	@Override
	public ProfileResult getProfileByUserId(final long userId) {

		final Profile profile = findProfileBy(userId);

		return new ProfileResult(
			profile.getId(),
			profile.getImageUrl(),
			profile.getUsername(),
			profile.getBio(),

			null, // TODO - 선호 카테고리 조회 : 처리 어떻게?

			//TODO - Follow 구현후 더미 값들 바꾸기
			0,
			0,
			false
		);
	}

	@Override
	public void updateProfile(final UpdateProfileCommand command) {

		final Profile profile = findProfileBy(command.getUserID());

		profile.update(
			command.getUsername(),
			command.getBio(),
			command.getImageUrl()
		);
	}

	private Profile findProfileBy(final long userId) {
		return profileRepository.findByUserId(userId).orElseThrow(LinkoceanRuntimeException::new);
	}
}
