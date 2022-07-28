package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.FindProfileCond;

@Repository
public class CustomProfileRepositoryImpl implements CustomProfileRepository {

	// TODO - implement after queryDsl setting
	@Override
	public List<Profile> findFollowerProfilesBy(final FindProfileCond findCond) {
		return null;
	}

	// TODO - implement after queryDsl setting
	@Override
	public List<Profile> findFolloweeProfilesBy(final FindProfileCond findCond) {
		return null;
	}
}
