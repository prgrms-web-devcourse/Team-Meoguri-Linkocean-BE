package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;

public interface CustomProfileRepository {

	List<Profile> findFollowerProfilesBy(ProfileFindCond findCond);

	List<Profile> findFolloweeProfilesBy(ProfileFindCond findCond);

	List<Profile> findByUsernameLike(ProfileFindCond findCond);

	Page<Profile> ultimateFindProfiles(UltimateProfileFindCond findCond, Pageable pageable);
}
