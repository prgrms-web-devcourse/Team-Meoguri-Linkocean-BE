package com.meoguri.linkocean.domain.profile.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class FollowRepositoryTest {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	private User user1;
	private User user2;

	private Profile profile1;
	private Profile profile2;

	@BeforeEach
	void setUp() {
		user1 = userRepository.save(new User("haha@gmail.com", "GOOGLE"));
		user2 = userRepository.save(new User("papa@gmail.com", "GOOGLE"));

		profile1 = profileRepository.save(new Profile(user1, "haha"));
		profile2 = profileRepository.save(new Profile(user2, "papa"));
	}

	@Test
	void 팔로우_여부_조회_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		final boolean follow1 = followRepository.existsByFollowerAndFollowee(follower, followee);
		final boolean follow2 = followRepository.existsByFollowerAndFollowee(followee, follower);

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
		final Optional<Follow> oFollow = followRepository.findByProfiles(follower, followee);

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
		int countProfile1Follower = followRepository.countFollowerByUserId(user1.getId());
		int countProfile1Followee = followRepository.countFolloweeByUserId(user1.getId());

		int countProfile2Follower = followRepository.countFollowerByUserId(user2.getId());
		int countProfile2Followee = followRepository.countFolloweeByUserId(user2.getId());

		//then
		assertThat(countProfile1Follower).isEqualTo(0);
		assertThat(countProfile1Followee).isEqualTo(1);

		assertThat(countProfile2Follower).isEqualTo(1);
		assertThat(countProfile2Followee).isEqualTo(0);
	}

	@Test
	void 팔로이_아이디_목록_조회_성공() {
		//given
		Profile follower = profile1;
		Profile followee = profile2;
		followRepository.save(new Follow(follower, followee));

		//when
		final List<Long> followeeIdsOfUser1 = followRepository.findAllFolloweeIdByFollowerId(profile1.getId());
		final List<Long> followeeIdsOfUser2 = followRepository.findAllFolloweeIdByFollowerId(profile2.getId());

		//then
		assertThat(followeeIdsOfUser1).containsExactly(profile2.getId());
		assertThat(followeeIdsOfUser2).isEmpty();
	}
}
