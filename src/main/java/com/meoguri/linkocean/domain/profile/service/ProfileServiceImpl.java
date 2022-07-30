package com.meoguri.linkocean.domain.profile.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
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

	@Override
	public List<SearchProfileResult> searchFollowerProfiles(final ProfileSearchCond searchCond) {

		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(searchCond.getUserId()).getId();

		final List<Profile> followerProfiles = profileRepository.findFollowerProfilesBy(
			new FindProfileCond(
				searchCond.getUserId(),
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		final List<Long> followeeProfileIds = followerProfiles.stream().map(Profile::getId).collect(toList());
		final List<Boolean> isFollows = checkIsFollows(currentUserProfileId, followeeProfileIds);

		return getResult(followerProfiles, isFollows);
	}

	@Override
	public List<SearchProfileResult> searchFolloweeProfiles(final ProfileSearchCond searchCond) {

		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(searchCond.getUserId()).getId();

		final List<Profile> followeeProfiles = profileRepository.findFolloweeProfilesBy(
			new FindProfileCond(
				currentUserProfileId,
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		return followeeProfiles.stream().map(p -> new SearchProfileResult(
			p.getId(),
			p.getUsername(),
			p.getImageUrl(),
			true // 현재 사용자가 팔로워인 상황이므로 팔로우 여부는 항상 true
		)).collect(toList());
	}

	@Override
	public boolean existsByUserId(final long userId) {
		return profileRepository.findByUserId(userId).isPresent();
	}

	/**
	 * 두 배열을 결과 배열로 변환하는 메서드
	 */
	private List<SearchProfileResult> getResult(final List<Profile> followerProfiles, final List<Boolean> isFollows) {
		int numFollower = followerProfiles.size();
		List<SearchProfileResult> result = new ArrayList<>();
		for (int i = 0; i < numFollower; i++) {
			final Profile followerProfile = followerProfiles.get(i);
			final boolean isFollow = isFollows.get(i);

			result.add(new SearchProfileResult(
				followerProfile.getId(),
				followerProfile.getUsername(),
				followerProfile.getImageUrl(),
				isFollow
			));
		}
		return result;
	}

	/**
	 * 팔로우 여부 조회
	 * @param profileId - 조회 하는 사용자의 프로필 아이디
	 * @param checkProfileIds - 조회 대상 사용자 들의 프로필 아이디 목록
	 * @return - 전달 받은 순서대로 팔로우 여부 반환
	 */
	private List<Boolean> checkIsFollows(Long profileId, List<Long> checkProfileIds) {

		final List<Long> followeeProfileIds = followRepository.findAllFolloweeIdByFollowerId(profileId);

		return checkProfileIds.stream().map(followeeProfileIds::contains).collect(toList());
	}
}
