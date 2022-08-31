package com.meoguri.linkocean.domain.profile.query.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;

public interface ProfileQueryRepository extends Repository<Profile, Long> {

	/**
	 * 다양한 조건으로 프로필 목록 조회
	 * - 팔로워 목록 조회
	 * - 팔로이 목록 조회
	 * - 특정 username 프로필 목록 조회
	 */
	Slice<Profile> findProfiles(ProfileFindCond findCond, Pageable pageable);
}
