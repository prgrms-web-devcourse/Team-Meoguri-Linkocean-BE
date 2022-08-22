package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.profile.entity.FavoriteCategories.*;
import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileServiceImpl implements ProfileService {

	private final UserService userService;

	private final ProfileRepository profileRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Transactional
	@Override
	public long registerProfile(final RegisterProfileCommand command) {
		final long userId = command.getUserId();
		final String username = command.getUsername();
		final FavoriteCategories favoriteCategories = new FavoriteCategories(command.getCategories());

		/* 비즈니스 로직 검증 - 프로필의 [유저 이름]은 중복 될 수 없다 */
		final boolean exists = profileRepository.existsByUsername(username);
		checkUniqueConstraint(exists, "이미 사용중인 이름입니다.");

		/* 프로필 저장, 유저에 프로필 등록 */
		final Profile profile = profileRepository.save(new Profile(username, favoriteCategories));
		userService.registerProfile(userId, profile);

		log.info("save profile with id :{}, username :{}", profile.getId(), username);
		return profile.getId();
	}

	@Override
	public GetDetailedProfileResult getByProfileId(final long currentProfileId, final long targetProfileId) {
		/* 프로필 조회 */
		final Profile profile = findProfileByIdQuery.findProfileFetchFollows(currentProfileId);
		final Profile target = findProfileByIdQuery.findById(targetProfileId);

		/* 추가 정보 조회 */
		final boolean isFollow = profile.checkIsFollow(target);
		final int followerCount = profileRepository.getFollowerCount(target);
		final int followeeCount = profileRepository.getFolloweeCount(target);

		/* 결과 반환 */
		return new GetDetailedProfileResult(
			target.getId(),
			target.getUsername(),
			target.getImage(),
			target.getBio(),
			toCategories(target.getFavoriteCategories()),
			isFollow,
			followerCount,
			followeeCount
		);
	}

	@Transactional
	@Override
	public void updateProfile(final UpdateProfileCommand command) {
		final long profileId = command.getProfileId();
		final String updateUsername = command.getUsername();

		/* 프로필 조회 */
		final Profile profile = findProfileByIdQuery.findById(profileId);

		/* 비즈니스 로직 검증 - 프로필의 [유저 이름]은 중복 될 수 없다 */
		final boolean exists = profileRepository.existsByUsernameExceptMe(updateUsername, profileId);
		checkUniqueConstraint(exists, "이미 사용중인 이름입니다.");

		/* 프로필 업데이트 */
		profile.update(
			updateUsername, command.getBio(), command.getImage(),
			new FavoriteCategories(command.getCategories())
		);
	}

	@Override
	public Slice<GetProfilesResult> getProfiles(
		final long currentProfileId,
		final ProfileFindCond findCond,
		final Pageable pageable
	) {
		final Profile currentProfile = findProfileByIdQuery.findProfileFetchFollows(currentProfileId);

		/* 프로필 목록 가져 오기 */
		final Slice<Profile> profilesSlice = profileRepository.findProfiles(findCond, pageable);
		final List<Profile> profiles = profilesSlice.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFollows = currentProfile.checkIsFollows(profiles);

		/* 결과 반환 */
		return toResultSlice(profiles, isFollows, pageable, profilesSlice.hasNext());
	}

	private Slice<GetProfilesResult> toResultSlice(
		final List<Profile> profiles,
		final List<Boolean> isFollows,
		Pageable pageable,
		boolean hasNext
	) {
		List<GetProfilesResult> results = new ArrayList<>();
		for (int i = 0; i < profiles.size(); i++) {
			final Profile profile = profiles.get(i);
			final boolean isFollow = isFollows.get(i);

			results.add(new GetProfilesResult(
				profile.getId(),
				profile.getUsername(),
				profile.getImage(),
				isFollow
			));
		}
		return new SliceImpl<>(results, pageable, hasNext);
	}
}
