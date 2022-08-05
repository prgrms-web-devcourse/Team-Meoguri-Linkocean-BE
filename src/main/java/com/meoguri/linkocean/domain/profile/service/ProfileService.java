package com.meoguri.linkocean.domain.profile.service;

import java.util.List;

import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;

public interface ProfileService {

	/* 프로필 등록 */
	long registerProfile(RegisterProfileCommand command);

	/* 내 프로필 조회 */
	GetMyProfileResult getMyProfile(long userId);

	/* 프로필 상세 조회 */
	GetDetailedProfileResult getByProfileId(long userId, long profileId);

	/* 프로필 업데이트 */
	void updateProfile(UpdateProfileCommand command);

	/**
	 * profileId 사용자의 팔로워 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	List<SearchProfileResult> searchFollowerProfiles(ProfileSearchCond searchCond, long profileId);

	/**
	 * profileId 사용자의 팔로이 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	List<SearchProfileResult> searchFolloweeProfiles(ProfileSearchCond searchCond, long profileId);

	/* 프로필 보유 여부 확인 */
	boolean existsByUserId(long userId);

	/* 프로필 목록 조회 - 머구리 찾기*/
	List<SearchProfileResult> searchProfilesByUsername(ProfileSearchCond searchCond);

}
