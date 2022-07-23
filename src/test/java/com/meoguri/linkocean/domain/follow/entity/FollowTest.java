package com.meoguri.linkocean.domain.follow.entity;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.profile.entity.Profile;

class FollowTest {

	@Test
	void 팔로우_생성_성공() {
		//given
		final Profile follower = createProfile();
		final Profile followee = createProfile();

		//when
		final Follow follow = new Follow(follower, followee);

		//then
		assertThat(follow).isNotNull()
			.extracting(Follow::getFollower, Follow::getFollowee)
			.containsExactly(follower, followee);
	}
}