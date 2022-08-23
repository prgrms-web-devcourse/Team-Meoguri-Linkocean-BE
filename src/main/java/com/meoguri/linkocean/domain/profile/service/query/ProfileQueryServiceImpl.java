package com.meoguri.linkocean.domain.profile.service.query;

import static com.meoguri.linkocean.domain.profile.entity.FavoriteCategories.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.command.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.command.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.command.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.command.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.command.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetProfilesResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

	private final ProfileRepository profileRepository;
	private final FollowRepository followRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final CheckIsFollowQuery checkIsFollowQuery;

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
			toCategories(targetProfile.getFavoriteCategories()),
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
		/* 프로필 목록 가져 오기 */
		final Slice<Profile> profilesSlice = profileRepository.findProfiles(findCond, pageable);
		final List<Profile> profiles = profilesSlice.getContent();

		/* 추가 정보 조회 */
		final List<Boolean> isFollows = getIsFollow(currentProfileId, profiles, findCond);

		/* 결과 반환 */
		return toResultSlice(profiles, isFollows, pageable, profilesSlice.hasNext());
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
