package com.meoguri.linkocean.domain.profile.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
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
	 * <li>팔로워 목록 조회</li>
	 * <li>팔로이 목록 조회</li>
	 * <li>특정 username 프로필 목록 조회</li>
	 */
	Page<GetProfilesResult> getProfiles(long currentProfileId, UltimateProfileFindCond searchCond, Pageable pageable);

	/**
	 * profileId 사용자의 팔로워 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	List<GetProfilesResult> searchFollowerProfiles(ProfileSearchCond searchCond, long profileId);

	/**
	 * profileId 사용자의 팔로이 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	List<GetProfilesResult> searchFolloweeProfiles(ProfileSearchCond searchCond, long profileId);

	/* 프로필 보유 여부 확인 */
	boolean existsByUserId(long userId);

	/* 프로필 목록 조회 - 머구리 찾기*/
	List<GetProfilesResult> searchProfilesByUsername(ProfileSearchCond searchCond);

}
