package com.meoguri.linkocean.domain.profile.persistence.command;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.command.dto.ProfileFindCond;

public interface CustomProfileRepository {

	/**
	 * 다양한 조건으로 프로필 목록 조회
	 * - 팔로워 목록 조회
	 * - 팔로이 목록 조회
	 * - 특정 username 프로필 목록 조회
	 */
	Slice<Profile> findProfiles(ProfileFindCond findCond, Pageable pageable);
}
