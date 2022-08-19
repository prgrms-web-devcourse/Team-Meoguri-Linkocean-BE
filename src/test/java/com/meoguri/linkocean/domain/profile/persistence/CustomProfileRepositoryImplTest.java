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
import org.springframework.data.domain.Pageable;
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

	private long profileId1;
	private long profileId2;
	private long profileId3;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		//set up 3 users
		profile1 = 사용자_프로필_저장_등록("user1@gmail.com", GOOGLE, "user1", IT);
		profile2 = 사용자_프로필_저장_등록("user2@naver.com", NAVER, "user2", IT);
		profile3 = 사용자_프로필_저장_등록("user3@kakao.com", KAKAO, "user3", IT);

		profileId1 = profile1.getId();
		profileId2 = profile2.getId();
		profileId3 = profile3.getId();

		pageable = createPageable();
	}

	/* profile 1 <-> profile 2 -> profile 3 */
	@Test
	void 팔로워_목록_조회_성공_이름_지정_X() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile2, profile1);
		팔로우_저장(profile2, profile3);

		ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).follower(true).build();
		ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).follower(true).build();
		ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).follower(true).build();

		//when
		final Slice<Profile> followerSlice1 = profileRepository.findProfiles(cond1, pageable);
		final Slice<Profile> followerSlice2 = profileRepository.findProfiles(cond2, pageable);
		final Slice<Profile> followerSlice3 = profileRepository.findProfiles(cond3, pageable);

		//then
		assertThat(followerSlice1).containsExactly(profile2);
		assertThat(followerSlice2).containsExactly(profile1);
		assertThat(followerSlice3).containsExactly(profile2);
	}

	/* profile1, profile 2 -> profile3 */
	@Test
	void 팔로워_목록_조회_성공_이름_지정() {
		//given
		팔로우_저장(profile1, profile3);
		팔로우_저장(profile2, profile3);

		ProfileFindCond cond = ProfileFindCond.builder()
			.username("user1")
			.profileId(profileId3)
			.follower(true)
			.build();

		//when
		final Slice<Profile> followerSlice = profileRepository.findProfiles(cond, pageable);

		//then
		assertThat(followerSlice).containsExactly(profile1);
	}

	/* profile 1 <-> profile2 -> profile3 */
	@Test
	void 팔로이_목록_조회_성공_이름_지정_X() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile2, profile1);
		팔로우_저장(profile2, profile3);

		ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).followee(true).build();
		ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).followee(true).build();
		ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).followee(true).build();

		//when
		final Slice<Profile> followeeSlice1 = profileRepository.findProfiles(cond1, pageable);
		final Slice<Profile> followeeSlice2 = profileRepository.findProfiles(cond2, pageable);
		final Slice<Profile> followeeSlice3 = profileRepository.findProfiles(cond3, pageable);

		//then
		assertThat(followeeSlice1).containsExactly(profile2);
		assertThat(followeeSlice2).containsExactly(profile1, profile3);
		assertThat(followeeSlice3).isEmpty();
	}

	/* profile1, profile 2 -> profile3 */
	@Test
	void 팔로이_목록_조회_성공_이름_지정() {
		//given
		팔로우_저장(profile1, profile2);
		팔로우_저장(profile1, profile3);

		ProfileFindCond cond = ProfileFindCond.builder()
			.username("user3")
			.profileId(profileId1)
			.followee(true)
			.build();

		//when
		final Slice<Profile> followerSlice = profileRepository.findProfiles(cond, pageable);

		//then
		assertThat(followerSlice).containsExactly(profile3);
	}

	@Test
	void 프로필_목록_조회_성공_이름_지정() {
		//given
		ProfileFindCond cond = ProfileFindCond.builder()
			.username("user")
			.build();

		//when
		final Slice<Profile> profiles = profileRepository.findProfiles(cond, pageable);

		//then
		assertAll(
			() -> assertThat(profiles).containsExactly(profile1, profile2, profile3),
			() -> assertThat(profiles.hasNext()).isFalse()
		);
	}

	@Test
	void 프로필_목록_조회_페이지_초과() {
		//given
		ProfileFindCond cond = ProfileFindCond.builder()
			.username("user")
			.build();
		final PageRequest pageableWithSize2 = PageRequest.of(0, 2);

		//when
		final Slice<Profile> profiles = profileRepository.findProfiles(cond, pageableWithSize2);

		//then
		assertAll(
			() -> assertThat(profiles.getSize()).isEqualTo(pageableWithSize2.getPageSize()),
			() -> assertThat(profiles.hasNext()).isTrue()
		);
	}

}
