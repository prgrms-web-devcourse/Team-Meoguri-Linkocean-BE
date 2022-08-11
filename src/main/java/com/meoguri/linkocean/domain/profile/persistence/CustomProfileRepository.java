package com.meoguri.linkocean.domain.profile.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;

public interface CustomProfileRepository {
	/* 프로필 목록 조회 */
	Page<Profile> ultimateFindProfiles(UltimateProfileFindCond findCond, Pageable pageable);
}
