package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;

@Repository
public class DummyFollowRepository
	implements FollowRepository {
	@Override
	public boolean existsByFollower_idAndFollowee(final long followerId, final Profile followee) {
		return false;
	}

	@Override
	public int countFollowerByProfile(final Profile profile) {
		return 0;
	}

	@Override
	public int countFolloweeByProfile(final Profile profile) {
		return 0;
	}

	@Override
	public Set<Long> findFolloweeIdsFollowedBy(final long profileId, final List<Profile> targets) {
		return null;
	}

	@Override
	public long deleteByFollower_idAndFollowee_id(final long followerId, final long followeeId) {
		return 0;
	}
}
