package com.meoguri.linkocean.domain.profile.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;

public interface ProfileService {

	/* 프로필 등록 */
	long registerProfile(RegisterProfileCommand command);

	/* 프로필 상세 조회 */
	GetDetailedProfileResult getByProfileId(long currentProfileId, long targetProfileId);

	/* 프로필 업데이트 */
	void updateProfile(UpdateProfileCommand command);

	/**
	 * 다양한 조건으로 프로필 목록 조회
	 * - 팔로워 목록 조회
	 * - 팔로이 목록 조회
	 * - 특정 username 프로필 목록 조회
	 */
	Page<GetProfilesResult> getProfiles(long currentProfileId, ProfileFindCond searchCond, Pageable pageable);

	/* 프로필 보유 여부 확인 */
	boolean existsByUserId(long userId);
}
