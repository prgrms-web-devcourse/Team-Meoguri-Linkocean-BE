package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.FindProfileCond;

public interface CustomProfileRepository {

	List<Profile> findFollowerProfilesBy(FindProfileCond findCond);

	List<Profile> findFolloweeProfilesBy(FindProfileCond findCond);
}
