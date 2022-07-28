package com.meoguri.linkocean.domain.profile.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.dto.FindProfileCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ProfileServiceImpl implements ProfileService {

	private final ProfileRepository profileRepository;
	private final FollowRepository followRepository;

	private final FindUserByIdQuery findUserByIdQuery;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;

	@Override
	public long registerProfile(final RegisterProfileCommand command) {

		final User user = findUserByIdQuery.findById(command.getUserId());

		// 프로필 등록
		final Profile profile = new Profile(user, command.getUsername());
		profileRepository.save(profile);

		// 선호 카테고리 등록
		command.getCategories().forEach(profile::addToFavoriteCategory);

		return profile.getId();
	}

	@Transactional(readOnly = true)
	@Override
	public GetMyProfileResult getMyProfile(final long userId) {

		final Profile profile = findProfileByUserIdQuery.findByUserId(userId);

		return new GetMyProfileResult(
			profile.getId(),
			profile.getUsername(),
			profile.getImageUrl(),
			profile.getBio(),
			profile.getMyFavoriteCategories(),
			followRepository.countFollowerByUserId(userId),
			followRepository.countFolloweeByUserId(userId),
			false
		);
	}

	@Override
	public void updateProfile(final UpdateProfileCommand command) {

		final Profile profile = findProfileByUserIdQuery.findByUserId(command.getUserId());

		// 프로필 업데이트
		profile.update(
			command.getUsername(),
			command.getBio(),
			command.getImageUrl()
		);

		// 선호 카테고리 업데이트
		profile.updateFavoriteCategories(command.getCategories());
	}

	// TODO - 팔로우 여부 조회 쿼리 도입하고 isFollow 채우기 (for loop 사용 불가피함)
	@Override
	public List<SearchProfileResult> searchFollowerProfiles(final ProfileSearchCond searchCond) {

		final List<Profile> profiles = profileRepository.findFollowerProfilesBy(
			new FindProfileCond(
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		return profiles.stream().map(p -> new SearchProfileResult(
			p.getId(),
			p.getUsername(),
			p.getImageUrl(),
			false
		)).collect(toList());
	}

	// TODO - 팔로우 여부 조회 쿼리 도입하고 isFollow 채우기 (for loop 사용 불가피함)
	@Override
	public List<SearchProfileResult> searchFolloweeProfiles(final ProfileSearchCond searchCond) {

		final List<Profile> profiles = profileRepository.findFolloweeProfilesBy(
			new FindProfileCond(
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		return profiles.stream().map(p -> new SearchProfileResult(
			p.getId(),
			p.getUsername(),
			p.getImageUrl(),
			false
		)).collect(toList());
	}
}
