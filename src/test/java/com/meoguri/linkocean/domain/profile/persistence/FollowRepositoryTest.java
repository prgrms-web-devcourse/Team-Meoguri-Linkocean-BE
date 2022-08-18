package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.support.common.Fixture.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@DataJpaTest
class FollowRepositoryTest {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	private Profile profile1;
	private Profile profile2;

	@BeforeEach
	void setUp() {
		User user1 = userRepository.save(createUser("haha@gmail.com", GOOGLE));
		User user2 = userRepository.save(createUser("papa@gmail.com", GOOGLE));

		profile1 = profileRepository.save(new Profile(user1, "haha"));
		profile2 = profileRepository.save(new Profile(user2, "papa"));
	}

	@Test
	void 팔로우_여부_조회_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		final boolean follow1 = followRepository.existsByFollower_idAndFollowee(follower.getId(), followee);
		final boolean follow2 = followRepository.existsByFollower_idAndFollowee(followee.getId(), follower);

		//then
		assertThat(follow1).isTrue();
		assertThat(follow2).isFalse();
	}

	@Test
	void 팔로워_팔로이_조합으로_조회_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		//when
		final Optional<Follow> oFollow = followRepository.findByFollowerAndFollowee(follower, followee);

		//then
		assertThat(oFollow).isPresent().get()
			.extracting(Follow::getFollower, Follow::getFollowee)
			.containsExactly(follower, followee);
	}

	@Test
	void 팔로워_팔로이_카운트_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		//when
		int countProfile1Follower = followRepository.countFollowerByProfile(follower);
		int countProfile1Followee = followRepository.countFolloweeByProfile(follower);

		int countProfile2Follower = followRepository.countFollowerByProfile(followee);
		int countProfile2Followee = followRepository.countFolloweeByProfile(followee);

		//then
		assertThat(countProfile1Follower).isEqualTo(0);
		assertThat(countProfile1Followee).isEqualTo(1);

		assertThat(countProfile2Follower).isEqualTo(1);
		assertThat(countProfile2Followee).isEqualTo(0);
	}

	@Test
	void 팔로이_아이디_집합_조회_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		//when
		final Set<Long> followeeIdsOfUser1 =
			followRepository.findFolloweeIdsFollowedBy(profile1.getId(), of(profile1, profile2));
		final Set<Long> followeeIdsOfUser2 =
			followRepository.findFolloweeIdsFollowedBy(profile2.getId(), of(profile1, profile2));

		//then
		assertThat(followeeIdsOfUser1).containsExactly(profile2.getId());
		assertThat(followeeIdsOfUser2).isEmpty();
	}
}
