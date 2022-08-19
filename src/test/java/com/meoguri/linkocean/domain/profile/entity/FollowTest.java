package com.meoguri.linkocean.domain.profile.entity;

import org.junit.jupiter.api.Test;

class FollowTest {

	@Test
	void 자기_자신_팔로우_실패() {
		//given
		final Profile follower = createProfile();
		final Profile followee = follower;

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> new Follow(follower, followee));
	}
}
