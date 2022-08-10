package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.Collections.*;
import static org.springframework.util.StringUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;
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
		final Profile targetProfile = findProfileByIdQuery.findById(targetProfileId);

		return new GetDetailedProfileResult(
			targetProfile.getId(),
			targetProfile.getUsername(),
			targetProfile.getImage(),
			targetProfile.getBio(),
			targetProfile.getMyFavoriteCategories(),
			checkIsFollowQuery.isFollow(currentProfileId, targetProfile),
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

	@Override
	public Page<SearchProfileResult> getProfiles(final long currentProfileId, final UltimateProfileFindCond findCond,
		final Pageable pageable) {

		/* 프로필 목록 가져 오기 */
		final Page<Profile> profilesPage = profileRepository.ultimateFindProfiles(findCond, pageable);
		final List<Profile> profiles = profilesPage.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFollows = getIsFollow(currentProfileId, profiles, findCond);

		return toResultPage(profiles, isFollows, pageable);
	}

	private List<Boolean> getIsFollow(final long currentProfileId, final List<Profile> profiles,
		final UltimateProfileFindCond findCond) {
		if (isMyFollowees(currentProfileId, findCond.getProfileId(), findCond.isFollowee())) {
			return new ArrayList<>((nCopies(profiles.size(), true)));
		}
		return checkIsFollowQuery.isFollows(currentProfileId, profiles);
	}

	private boolean isMyFollowees(final long currentProfileId, final Long profileId, final boolean followee) {
		return followee && currentProfileId == profileId; /* 순서 중요!! 순서 바뀌면 NPE 가능성 존재 */
	}

	private Page<SearchProfileResult> toResultPage(final List<Profile> profiles, final List<Boolean> isFollows,
		Pageable pageable) {

		List<SearchProfileResult> results = new ArrayList<>();
		for (int i = 0; i < profiles.size(); i++) {
			final Profile followerProfile = profiles.get(i);
			final boolean isFollow = isFollows.get(i);

			results.add(new SearchProfileResult(
				followerProfile.getId(),
				followerProfile.getUsername(),
				followerProfile.getImage(),
				isFollow
			));
		}
		return new PageImpl<>(results, pageable, 0L);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SearchProfileResult> searchFollowerProfiles(final ProfileSearchCond searchCond, final long profileId) {
		final long currentUserProfileId = searchCond.getProfileId();

		// 프로필 조회
		final List<Profile> followerProfiles = profileRepository.findFollowerProfilesBy(
			new ProfileFindCond(
				profileId,
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		// 추가 정보 조회
		final List<Boolean> isFollows = checkIsFollowQuery.isFollows(currentUserProfileId, followerProfiles);

		// 결과 반환
		return getResult(followerProfiles, isFollows);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SearchProfileResult> searchFolloweeProfiles(final ProfileSearchCond searchCond, final long profileId) {
		final long currentUserProfileId = searchCond.getProfileId();

		// 프로필 조회
		final List<Profile> followeeProfiles = profileRepository.findFolloweeProfilesBy(
			new ProfileFindCond(
				profileId,
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			)
		);

		// 추가 정보 조회
		final List<Boolean> isFollows =
			currentUserProfileId == profileId
			? new ArrayList<>(nCopies(followeeProfiles.size(), true)) //자신의 팔로이 탭을 누른 경우 팔로우 여부는 항상 true 이다
			: checkIsFollowQuery.isFollows(currentUserProfileId, followeeProfiles);

		// 결과 반환
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

		// 프로필 조회
		List<Profile> profiles = profileRepository.findByUsernameLike(
			new ProfileFindCond(
				searchCond.getPage(),
				searchCond.getSize(),
				searchCond.getUsername()
			));

		// 추가 정보 조회
		final List<Boolean> isFollows = checkIsFollowQuery.isFollows(searchCond.getProfileId(), profiles);

		// 결과 반환
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

}
