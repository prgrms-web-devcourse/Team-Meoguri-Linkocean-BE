package com.meoguri.linkocean.domain.profile.query.service;

import static com.meoguri.linkocean.domain.profile.entity.FavoriteCategories.*;
import static java.lang.String.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.ProfileQueryRepository;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfilesResult;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

	private final ProfileQueryRepository profileQueryRepository;

	@Override
	public GetDetailedProfileResult getByProfileId(final long currentProfileId, final long targetProfileId) {
		/* 프로필 조회 */
		final Profile profile = findProfileFetchFollows(currentProfileId);
		final Profile target = findById(targetProfileId);

		/* 추가 정보 조회 */
		final boolean isFollow = profile.isFollow(target);
		final int followerCount = profileQueryRepository.getFollowerCount(target);
		final int followeeCount = profileQueryRepository.getFolloweeCount(target);

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
		final Profile currentProfile = findProfileFetchFollows(currentProfileId);

		/* 프로필 목록 가져 오기 */
		final Slice<Profile> profilesSlice = profileQueryRepository.findProfiles(findCond, pageable);
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

	@Override
	public Profile findById(final long profileId) {
		return findProfileById(profileId, profileQueryRepository::findById);
	}

	@Override
	public Profile findProfileFetchFavoriteById(final long profileId) {
		return findProfileById(profileId, profileQueryRepository::findProfileFetchFavoriteIdsById);
	}

	@Override
	public Profile findProfileFetchFollows(final long profileId) {
		return findProfileById(profileId, profileQueryRepository::findProfileFetchFollows);
	}

	private Profile findProfileById(long profileId, LongFunction<Optional<Profile>> findById) {
		return findById.apply(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}
}
