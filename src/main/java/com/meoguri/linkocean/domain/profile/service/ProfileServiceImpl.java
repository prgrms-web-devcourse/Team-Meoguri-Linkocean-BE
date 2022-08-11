package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.FindUserByIdQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileServiceImpl implements ProfileService {

	private final ProfileRepository profileRepository;
	private final FollowRepository followRepository;

	private final FindUserByIdQuery findUserByIdQuery;
	private final FindProfileByIdQuery findProfileByIdQuery;
	private final CheckIsFollowQuery checkIsFollowQuery;

	@Transactional
	@Override
	public long registerProfile(final RegisterProfileCommand command) {
		final long userId = command.getUserId();
		final String username = command.getUsername();
		final List<Category> categories = command.getCategories();

		/* 비즈니스 로직 검증 - 프로필의 [유저 이름]은 중복 될 수 없다 */
		final boolean exists = profileRepository.existsByUsername(username);
		checkUniqueConstraint(exists, "이미 사용중인 이름입니다.");

		/* 연관 관계 조회 */
		final User user = findUserByIdQuery.findById(userId);

		/* 프로필 등록 */
		final Profile profile = profileRepository.save(new Profile(user, username));
		log.info("save profile with id :{}, username :{}", profile.getId(), profile.getUsername());

		/* 선호 카테고리 등록 */
		categories.forEach(profile::addToFavoriteCategory);
		return profile.getId();
	}

	@Override
	public GetDetailedProfileResult getByProfileId(final long currentProfileId, final long targetProfileId) {
		/* 프로필 조회 */
		final Profile targetProfile = findProfileByIdQuery.findById(targetProfileId);

		/* 추가 정보 조회 */
		final boolean isFollow = checkIsFollowQuery.isFollow(currentProfileId, targetProfile);
		final int followerCount = followRepository.countFollowerByProfile(targetProfile);
		final int followeeCount = followRepository.countFolloweeByProfile(targetProfile);

		/* 결과 반환 */
		return new GetDetailedProfileResult(
			targetProfile.getId(),
			targetProfile.getUsername(),
			targetProfile.getImage(),
			targetProfile.getBio(),
			targetProfile.getMyFavoriteCategories(),
			isFollow,
			followerCount,
			followeeCount
		);
	}

	@Transactional
	@Override
	public void updateProfile(final UpdateProfileCommand command) {
		/* 프로필 조회 */
		final Profile profile = findProfileByIdQuery.findById(command.getProfileId());

		/* 프로필 업데이트 */
		profile.update(command.getUsername(), command.getBio(), command.getImage());

		/* 선호 카테고리 업데이트 */
		profile.updateFavoriteCategories(command.getCategories());
	}

	@Override
	public Page<GetProfilesResult> getProfiles(
		final long currentProfileId,
		final ProfileFindCond findCond,
		final Pageable pageable
	) {
		/* 프로필 목록 가져 오기 */
		final Page<Profile> profilesPage = profileRepository.findProfiles(findCond, pageable);
		final List<Profile> profiles = profilesPage.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFollows = getIsFollow(currentProfileId, profiles, findCond);

		return toResultPage(profiles, isFollows, pageable);
	}

	private List<Boolean> getIsFollow(
		final long currentProfileId,
		final List<Profile> profiles,
		final ProfileFindCond findCond
	) {
		if (isMyFollowees(currentProfileId, findCond.getProfileId(), findCond.isFollowee())) {
			return new ArrayList<>((nCopies(profiles.size(), true)));
		}
		return checkIsFollowQuery.isFollows(currentProfileId, profiles);
	}

	private boolean isMyFollowees(final long currentProfileId, final Long profileId, final boolean followee) {
		return followee && currentProfileId == profileId; /* 순서 중요!! 순서 바뀌면 NPE 가능성 존재 */
	}

	private Page<GetProfilesResult> toResultPage(
		final List<Profile> profiles,
		final List<Boolean> isFollows,
		Pageable pageable
	) {
		List<GetProfilesResult> results = new ArrayList<>();
		for (int i = 0; i < profiles.size(); i++) {
			final Profile followerProfile = profiles.get(i);
			final boolean isFollow = isFollows.get(i);

			results.add(new GetProfilesResult(
				followerProfile.getId(),
				followerProfile.getUsername(),
				followerProfile.getImage(),
				isFollow
			));
		}
		return new PageImpl<>(results, pageable, 0L);
	}

	@Override
	public boolean existsByUserId(final long userId) {
		return profileRepository.findByUserId(userId).isPresent();
	}
}
