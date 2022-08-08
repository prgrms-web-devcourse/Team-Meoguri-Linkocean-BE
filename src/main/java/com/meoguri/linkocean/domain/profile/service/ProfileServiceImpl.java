package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByIdQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ProfileServiceImpl implements ProfileService {

	private final ProfileRepository profileRepository;
	private final FollowRepository followRepository;

	private final FindUserByIdQuery findUserByIdQuery;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;
	private final CheckIsFollowQuery checkIsFollowQuery;

	@Override
	public long registerProfile(final RegisterProfileCommand command) {
		final User user = findUserByIdQuery.findById(command.getUserId());

		// 프로필 등록
		final Profile profile = profileRepository.save(new Profile(user, command.getUsername()));
		log.info("save profile with id :{}, username :{}", profile.getId(), profile.getUsername());

		// 선호 카테고리 등록
		command.getCategories().forEach(profile::addToFavoriteCategory);
		return profile.getId();
	}

	@Transactional(readOnly = true)
	@Override
	public GetMyProfileResult getMyProfile(final long profileId) {
		final Profile profile = findProfileByIdQuery.findById(profileId);

		return new GetMyProfileResult(
			profile.getId(),
			profile.getUsername(),
			profile.getImage(),
			profile.getBio(),
			profile.getMyFavoriteCategories(),
			followRepository.countFollowerByProfile(profile),
			followRepository.countFolloweeByProfile(profile)
		);
	}

	@Transactional(readOnly = true)
	@Override
	public GetDetailedProfileResult getByProfileId(final long currentProfileId, final long targetProfileId) {
		// final Profile currentUser = findProfileByUserIdQuery.findByUserId(userId);
		final Profile currentProfile = findProfileByIdQuery.findById(currentProfileId);
		final Profile targetProfile = findProfileByIdQuery.findById(targetProfileId);

		return new GetDetailedProfileResult(
			targetProfile.getId(),
			targetProfile.getUsername(),
			targetProfile.getImage(),
			targetProfile.getBio(),
			targetProfile.getMyFavoriteCategories(),
			checkIsFollowQuery.isFollow(currentProfile, targetProfile),
			followRepository.countFollowerByProfile(targetProfile),
			followRepository.countFolloweeByProfile(targetProfile)
		);
	}

	@Override
	public void updateProfile(final UpdateProfileCommand command) {
		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());
		final String origUsername = profile.getUsername();

		// 프로필 업데이트
		profile.update(command.getUsername(), command.getBio(), command.getImage());

		// 선호 카테고리 업데이트
		profile.updateFavoriteCategories(command.getCategories());
		log.info("profile updated : from username : {} -> {}", origUsername, profile.getUsername());
	}

	@Transactional(readOnly = true)
	@Override
	public List<SearchProfileResult> searchFollowerProfiles(final ProfileSearchCond searchCond, final long profileId) {
		// 1 단계 - 검색 대상 프로필의 팔로워 목록 조회
		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(searchCond.getUserId()).getId();
		final List<Profile> followerProfiles = profileRepository.findFollowerProfilesBy(
			new ProfileFindCond(
				profileId,
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		// 2 단계 - 검색된 프로필에 대해서 현재 사용자의 팔로우 여부를 말아준다
		final List<Long> followerProfileIds = followerProfiles.stream().map(Profile::getId).collect(toList());
		final List<Boolean> isFollows = checkIsFollows(currentUserProfileId, followerProfileIds);
		return getResult(followerProfiles, isFollows);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SearchProfileResult> searchFolloweeProfiles(final ProfileSearchCond searchCond, final long profileId) {
		// 1 단계 - 검색 대상 프로필의 팔로이 목록 조회
		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(searchCond.getUserId()).getId();
		final List<Profile> followeeProfiles = profileRepository.findFolloweeProfilesBy(
			new ProfileFindCond(
				profileId,
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		// 2단계 - 경우에 따라 현재 사용자의 팔로우 여부를 획득한다
		final List<Long> followeeProfileIds = followeeProfiles.stream().map(Profile::getId).collect(toList());
		final List<Boolean> isFollows = currentUserProfileId == profileId
										? new ArrayList<>(nCopies(followeeProfiles.size(), true))
										//	자기자신의 팔로이 탭을 누른 경우 팔로우 여부는 항상 true 이다
										: checkIsFollows(currentUserProfileId, followeeProfileIds);

		// 3단계 - 결과를 말아 준다
		return getResult(followeeProfiles, isFollows);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsByUserId(final long userId) {
		return profileRepository.findByUserId(userId).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public List<SearchProfileResult> searchProfilesByUsername(final ProfileSearchCond searchCond) {
		checkArgument(hasText(searchCond.getUsername()), "사용자 이름을 입력해 주세요");

		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(searchCond.getUserId()).getId();
		List<Profile> profiles = profileRepository.findByUsernameLike(
			new ProfileFindCond(
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			));

		final List<Long> profilesId = profiles.stream().map(Profile::getId).collect(toList());
		final List<Boolean> isFollows = checkIsFollows(currentUserProfileId, profilesId);

		return getResult(profiles, isFollows);
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
				followerProfile.getImage(),
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
