package com.meoguri.linkocean.domain.profile.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;

public interface CustomProfileRepository {

	/* 사용자 이름 중복 확인 - 프로필 등록시 사용 */
	boolean existsByUsername(String username);

	/* profileId 의 것이 아닌 사용자 이름 중 updateUsername 의 중복 확인 - 프로필 수정시 사용 */
	boolean existsByUsernameExceptMe(String updateUsername, long profileId);

	/**
	 * 다양한 조건으로 프로필 목록 조회
	 * - 팔로워 목록 조회
	 * - 팔로이 목록 조회
	 * - 특정 username 프로필 목록 조회
	 */
	Slice<Profile> findProfiles(ProfileFindCond findCond, Pageable pageable);
}
