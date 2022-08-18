package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

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

	private Profile follower;
	private Profile followee;

	@BeforeEach
	void setUp() {
		final User follower = userRepository.save(createUser("follower@gmail.com", GOOGLE));
		final User followee = userRepository.save(createUser("followee@gmail.com", GOOGLE));

		this.follower = profileRepository.save(createProfile(follower, "follower"));
		this.followee = profileRepository.save(createProfile(followee, "followee"));
	}

	@Test
	void 팔로우_여부_체크_성공() {
		//given
		followRepository.save(new Follow(follower, followee));

		//when
		final boolean follow1 = query.isFollow(follower.getId(), followee);
		final boolean follow2 = query.isFollow(followee.getId(), follower);

		//then
		assertThat(follow1).isTrue();
		assertThat(follow2).isFalse();
	}

	@Test
	void 팔로이_아이디_집합_조회_성공() {
		//given
		followRepository.save(new Follow(follower, followee));

		//when
		final List<Boolean> followeeIdsOfUser1 = query.isFollows(follower.getId(), of(follower, followee));
		final List<Boolean> followeeIdsOfUser2 = query.isFollows(followee.getId(), of(follower, followee));

		//then
		assertThat(followeeIdsOfUser1).containsExactly(false, true);
		assertThat(followeeIdsOfUser2).containsExactly(false, false);
	}
}
