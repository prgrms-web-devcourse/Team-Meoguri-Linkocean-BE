package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;

public interface CustomProfileRepository {

	List<Profile> findFollowerProfilesBy(ProfileFindCond findCond);

	List<Profile> findFolloweeProfilesBy(ProfileFindCond findCond);

	List<Profile> findByUsernameLike(ProfileFindCond findCond);
}
