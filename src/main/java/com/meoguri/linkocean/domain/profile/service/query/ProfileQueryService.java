package com.meoguri.linkocean.domain.profile.service.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.persistence.command.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetProfilesResult;

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
}
