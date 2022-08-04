package com.meoguri.linkocean.domain.profile.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.FindProfileCond;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class CustomProfileRepositoryImplTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRepository followRepository;

	private Profile profile1;
	private Profile profile2;
	private Profile profile3;

	@BeforeEach
	void setUp() {
		//set up 3 users
		User user1 = userRepository.save(new User("user1@gmail.com", "GOOGLE"));
		User user2 = userRepository.save(new User("user2@naver.com", "NAVER"));
		User user3 = userRepository.save(new User("user3@kakao.com", "KAKAO"));

		profile1 = profileRepository.save(new Profile(user1, "user1"));
		profile2 = profileRepository.save(new Profile(user2, "user2"));
		profile3 = profileRepository.save(new Profile(user3, "user3"));
	}

	@Test
	void 팔로워_목록조회_성공_이름_지정_X() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final List<Profile> followerOfUser1 = profileRepository.findFollowerProfilesBy(findCond(profile1));
		final List<Profile> followerOfUser2 = profileRepository.findFollowerProfilesBy(findCond(profile2));
		final List<Profile> followerOfUser3 = profileRepository.findFollowerProfilesBy(findCond(profile3));

		//then
		assertThat(followerOfUser1).isEmpty();
		assertThat(followerOfUser2).containsExactly(profile1);
		assertThat(followerOfUser3).containsExactly(profile1, profile2);
	}

	@Test
	void 팔로워_목록조회_성공_이름_지정() {
		//given
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final List<Profile> followerOfUser1 = profileRepository.findFollowerProfilesBy(findCond(profile3, "user1"));

		//then
		assertThat(followerOfUser1).containsExactly(profile1);
	}

	@Test
	void 팔로이_목록조회_성공_이름_지정_X() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final List<Profile> followerOfUser1 = profileRepository.findFolloweeProfilesBy(findCond(profile1));
		final List<Profile> followerOfUser2 = profileRepository.findFolloweeProfilesBy(findCond(profile2));
		final List<Profile> followerOfUser3 = profileRepository.findFolloweeProfilesBy(findCond(profile3));

		//then
		assertThat(followerOfUser1).containsExactly(profile2, profile3);
		assertThat(followerOfUser2).containsExactly(profile3);
		assertThat(followerOfUser3).isEmpty();
	}

	@Test
	void 팔로이_목록조회_성공_이름_지정() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));

		//when
		final List<Profile> followerOfUser1 = profileRepository.findFolloweeProfilesBy(findCond(profile1, "user3"));

		//then
		assertThat(followerOfUser1).containsExactly(profile3);
	}

	@Test
	void 프로필_목록조회_성공_이름_지정() {
		//when
		final List<Profile> profiles = profileRepository.findByUsernameLike(new FindProfileCond(1, 8, "user"));

		//then
		assertThat(profiles).containsExactly(profile1, profile2, profile3);
	}

	private FindProfileCond findCond(final Profile profile, final String username) {
		return new FindProfileCond(profile.getId(), 1, 8, username);
	}

	private FindProfileCond findCond(final Profile profile) {
		return findCond(profile, null);
	}
}
