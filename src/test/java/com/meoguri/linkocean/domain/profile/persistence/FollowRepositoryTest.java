package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

class FollowRepositoryTest extends BasePersistenceTest {

	@Autowired
	private FollowRepository followRepository;

	private Profile profile1;
	private Profile profile2;

	private long profileId1;
	private long profileId2;

	@BeforeEach
	void setUp() {
		profile1 = 사용자_프로필_저장_등록("haha@gmail.com", GOOGLE, "haha", IT);
		profile2 = 사용자_프로필_저장_등록("papa@gmail.com", GOOGLE, "papa", IT);

		profileId1 = profile1.getId();
		profileId2 = profile2.getId();
	}

	@Test
	void 팔로우_여부_조회_성공() {
		//given
		팔로우_저장(profile1, profile2);

		//when
		final boolean follow1 = followRepository.existsByFollower_idAndFollowee(profileId1, profile2);
		final boolean follow2 = followRepository.existsByFollower_idAndFollowee(profileId2, profile1);

		//then
		assertThat(follow1).isTrue();
		assertThat(follow2).isFalse();
	}

	@Test
	void 팔로워_팔로이_조합으로_조회_성공() {
		//given
		팔로우_저장(profile1, profile2);

		//when
		final Optional<Follow> oFollow = followRepository.findByFollowerAndFollowee(profile1, profile2);

		//then
		assertThat(oFollow).isPresent();
		assertThat(oFollow.get().getFollower()).isEqualTo(profile1);
		assertThat(oFollow.get().getFollowee()).isEqualTo(profile2);
	}

	@Test
	void 팔로워_팔로이_카운트_성공() {
		//given
		팔로우_저장(profile1, profile2);

		//when
		int countProfile1Follower = followRepository.countFollowerByProfile(profile1);
		int countProfile1Followee = followRepository.countFolloweeByProfile(profile1);

		int countProfile2Follower = followRepository.countFollowerByProfile(profile2);
		int countProfile2Followee = followRepository.countFolloweeByProfile(profile2);

		//then
		assertThat(countProfile1Follower).isEqualTo(0);
		assertThat(countProfile1Followee).isEqualTo(1);

		assertThat(countProfile2Follower).isEqualTo(1);
		assertThat(countProfile2Followee).isEqualTo(0);
	}

	@Test
	void 팔로이_아이디_집합_조회_성공() {
		//given
		팔로우_저장(profile1, profile2);

		//when
		final Set<Long> followeeIds1 = followRepository.findFolloweeIdsFollowedBy(profileId1, of(profile1, profile2));
		final Set<Long> followeeIds2 = followRepository.findFolloweeIdsFollowedBy(profileId2, of(profile1, profile2));

		//then
		assertThat(followeeIds1).containsExactly(profileId2);
		assertThat(followeeIds2).isEmpty();
	}
}
