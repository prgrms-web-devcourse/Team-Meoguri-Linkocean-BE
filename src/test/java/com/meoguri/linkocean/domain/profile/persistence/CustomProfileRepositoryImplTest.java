package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import com.meoguri.linkocean.common.CustomP6spySqlFormat;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(CustomP6spySqlFormat.class)
@DataJpaTest
class CustomProfileRepositoryImplTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRepository followRepository;

	@PersistenceContext
	private EntityManager em;

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
	void 팔로워_목록_조회_성공_이름_지정_X() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final Page<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowers(profile1.getId()), defaultPageable());
		final Page<Profile> followerOfUser2 = profileRepository.findProfiles(
			condWhenFindFollowers(profile2.getId()), defaultPageable());
		final Page<Profile> followerOfUser3 = profileRepository.findProfiles(
			condWhenFindFollowers(profile3.getId()), defaultPageable());

		//then
		assertThat(followerOfUser1).isEmpty();
		assertThat(followerOfUser2).containsExactly(profile1);
		assertThat(followerOfUser3).containsExactly(profile1, profile2);
	}

	@Test
	void 팔로워_목록_조회_성공_이름_지정() {
		//given
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final Page<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowers(profile3.getId(), "user1"), defaultPageable());

		//then
		assertThat(followerOfUser1).containsExactly(profile1);
	}

	@Test
	void 팔로이_목록_조회_성공_이름_지정_X() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));
		followRepository.save(new Follow(profile2, profile3));

		//when
		final Page<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowees(profile1.getId()), defaultPageable());
		final Page<Profile> followerOfUser2 = profileRepository.findProfiles(
			condWhenFindFollowees(profile2.getId()), defaultPageable());
		final Page<Profile> followerOfUser3 = profileRepository.findProfiles(
			condWhenFindFollowees(profile3.getId()), defaultPageable());

		//then
		assertThat(followerOfUser1).containsExactly(profile2, profile3);
		assertThat(followerOfUser2).containsExactly(profile3);
		assertThat(followerOfUser3).isEmpty();
	}

	@Test
	void 팔로이_목록_조회_성공_이름_지정() {
		//given
		followRepository.save(new Follow(profile1, profile2));
		followRepository.save(new Follow(profile1, profile3));

		//when
		final Page<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowees(profile1.getId(), "user3"), defaultPageable());

		//then
		assertThat(followerOfUser1).containsExactly(profile3);
	}

	@Test
	void 프로필_목록_조회_성공_이름_지정() {
		//when
		final Page<Profile> profiles = profileRepository.findProfiles(condWhenFindUsingUsername("user"),
			defaultPageable());

		//then
		assertThat(profiles).containsExactly(profile1, profile2, profile3);
	}

	private ProfileFindCond condWhenFindUsingUsername(final String username) {
		return ProfileFindCond.builder().username(username).build();
	}

	private ProfileFindCond condWhenFindFollowees(final long profileId) {
		return condWhenFindFollowees(profileId, null);
	}

	private ProfileFindCond condWhenFindFollowees(final long profileId, final String username) {
		return ProfileFindCond.builder().profileId(profileId).followee(true).username(username).build();
	}

	private ProfileFindCond condWhenFindFollowers(final long profileId) {
		return condWhenFindFollowers(profileId, null);
	}

	private ProfileFindCond condWhenFindFollowers(final long profileId, final String username) {
		return ProfileFindCond.builder().profileId(profileId).follower(true).username(username).build();
	}
}
