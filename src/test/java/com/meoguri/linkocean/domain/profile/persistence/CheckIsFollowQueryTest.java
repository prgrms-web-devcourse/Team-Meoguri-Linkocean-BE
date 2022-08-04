package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(CheckIsFollowQuery.class)
@DataJpaTest
class CheckIsFollowQueryTest {

	@Autowired
	private CheckIsFollowQuery query;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	private Profile followerProfile;
	private Profile followeeProfile;

	@BeforeEach
	void setUp() {
		final User follower = userRepository.save(new User("follower@gmail.com", "GOOGLE"));
		final User followee = userRepository.save(new User("followee@gmail.com", "GOOGLE"));

		followerProfile = profileRepository.save(createProfile(follower, "follower"));
		followeeProfile = profileRepository.save(createProfile(followee, "followee"));
	}

	@Test
	void 팔로우_여부_체크_성공() {
		//given
		followRepository.save(new Follow(followerProfile, followeeProfile));

		//when
		final boolean follow1 = query.isFollow(followerProfile, followeeProfile);
		final boolean follow2 = query.isFollow(followeeProfile, followerProfile);

		//then
		assertThat(follow1).isTrue();
		assertThat(follow2).isFalse();
	}
}
