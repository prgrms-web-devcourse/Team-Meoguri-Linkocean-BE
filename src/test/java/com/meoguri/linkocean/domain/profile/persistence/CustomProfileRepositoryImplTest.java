package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.support.common.CustomP6spySqlFormat;
import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

@Import(CustomP6spySqlFormat.class)
class CustomProfileRepositoryImplTest extends BasePersistenceTest {

	@Autowired
	private ProfileRepository profileRepository;

	private Profile profile1;
	private Profile profile2;
	private Profile profile3;

	@BeforeEach
	void setUp() {
		//set up 3 users
		profile1 = 사용자_프로필_저장_등록("user1@gmail.com", GOOGLE, "user1", IT);
		profile2 = 사용자_프로필_저장_등록("user2@naver.com", NAVER, "user2", IT);
		profile3 = 사용자_프로필_저장_등록("user3@kakao.com", KAKAO, "user3", IT);
	}

	@Test
	void 팔로워_목록_조회_성공_이름_지정_X() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile1, profile3);
		팔로우_저장(profile2, profile3);

		//when
		final Slice<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowers(profile1.getId()), defaultPageable());
		final Slice<Profile> followerOfUser2 = profileRepository.findProfiles(
			condWhenFindFollowers(profile2.getId()), defaultPageable());
		final Slice<Profile> followerOfUser3 = profileRepository.findProfiles(
			condWhenFindFollowers(profile3.getId()), defaultPageable());

		//then
		assertThat(followerOfUser1).isEmpty();
		assertThat(followerOfUser2).containsExactly(profile1);
		assertThat(followerOfUser3).containsExactly(profile1, profile2);
	}

	@Test
	void 팔로워_목록_조회_성공_이름_지정() {
		//given
		팔로우_저장(profile1, profile3);
		팔로우_저장(profile2, profile3);

		//when
		final Slice<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowers(profile3.getId(), "user1"), defaultPageable());

		//then
		assertThat(followerOfUser1).containsExactly(profile1);
	}

	@Test
	void 팔로이_목록_조회_성공_이름_지정_X() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile1, profile3);
		팔로우_저장(profile2, profile3);

		//when
		final Slice<Profile> followeeOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowees(profile1.getId()), defaultPageable());
		final Slice<Profile> followeeOfUser2 = profileRepository.findProfiles(
			condWhenFindFollowees(profile2.getId()), defaultPageable());
		final Slice<Profile> followeeOfUser3 = profileRepository.findProfiles(
			condWhenFindFollowees(profile3.getId()), defaultPageable());

		//then
		assertThat(followeeOfUser1).containsExactly(profile2, profile3);
		assertThat(followeeOfUser2).containsExactly(profile3);
		assertThat(followeeOfUser3).isEmpty();
	}

	@Test
	void 팔로이_목록_조회_성공_이름_지정() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile1, profile3);

		//when
		final Slice<Profile> followerOfUser1 = profileRepository.findProfiles(
			condWhenFindFollowees(profile1.getId(), "user3"), defaultPageable());

		//then
		assertThat(followerOfUser1).containsExactly(profile3);
	}

	@Test
	void 프로필_목록_조회_성공_이름_지정() {
		//when
		final Slice<Profile> profiles = profileRepository.findProfiles(condWhenFindUsingUsername("user"),
			defaultPageable());

		//then
		assertAll(
			() -> assertThat(profiles).containsExactly(profile1, profile2, profile3),
			() -> assertThat(profiles.hasNext()).isFalse()
		);
	}

	@Test
	void 프로필_목록_조회_다음_페이지_있음() {
		//given
		final PageRequest pageable = PageRequest.of(0, 2);

		//when
		final Slice<Profile> profiles = profileRepository.findProfiles(condWhenFindUsingUsername("user"), pageable);

		//then
		assertAll(
			() -> assertThat(profiles.getSize()).isEqualTo(pageable.getPageSize()),
			() -> assertThat(profiles.hasNext()).isTrue()
		);
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
