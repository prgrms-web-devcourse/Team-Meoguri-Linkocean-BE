package com.meoguri.linkocean.domain.profile.query.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfilesResult;

public interface ProfileQueryService {

	/* 프로필 상세 조회 */
	GetDetailedProfileResult getByProfileId(long currentProfileId, long targetProfileId);

	/**
	 * 다양한 조건으로 프로필 목록 조회
	 * - 팔로워 목록 조회
	 * - 팔로이 목록 조회
	 * - 특정 username 프로필 목록 조회
	 */
	Slice<GetProfilesResult> getProfiles(long currentProfileId, ProfileFindCond searchCond, Pageable pageable);

	Profile findById(long profileId);

	Profile findProfileFetchFavoriteById(long profileId);

	Profile findProfileFetchFollows(long profileId);
}
