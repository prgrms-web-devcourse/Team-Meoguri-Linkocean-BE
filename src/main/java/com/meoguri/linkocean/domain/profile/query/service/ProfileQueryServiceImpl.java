package com.meoguri.linkocean.domain.profile.query.service;

import static com.meoguri.linkocean.domain.profile.entity.FavoriteCategories.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.command.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.command.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.ProfileDao;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfilesResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

	private final ProfileDao profileDao;

	private final ProfileRepository profileRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Override
	public GetDetailedProfileResult getByProfileId(final long currentProfileId, final long targetProfileId) {
		/* 프로필 조회 */
		final Profile profile = findProfileByIdQuery.findProfileFetchFollows(currentProfileId);
		final Profile target = findProfileByIdQuery.findById(targetProfileId);

		/* 추가 정보 조회 */
		final boolean isFollow = profile.isFollow(target);
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

	@Override
	public Slice<GetProfilesResult> getProfiles(
		final long currentProfileId,
		final ProfileFindCond findCond,
		final Pageable pageable
	) {
		final Profile currentProfile = findProfileByIdQuery.findProfileFetchFollows(currentProfileId);

		/* 프로필 목록 가져 오기 */
		final Slice<Profile> profilesSlice = profileDao.findProfiles(findCond, pageable);
		final List<Profile> profiles = profilesSlice.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFollows = currentProfile.isFollows(profiles);

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
