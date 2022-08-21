package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class ProfileServiceImplTest extends BaseServiceTest {

	@Autowired
	private ProfileService profileService;

	@Nested
	class 프로필_등록_수정_조회 {

		private Profile profile;
		private long profileId;

		@BeforeEach
		void setUp() {
			profile = 사용자_프로필_동시_저장_등록("haha@gmail.com", GOOGLE, "haha", IT);
			profileId = profile.getId();
		}

		@Test
		void 프로필_등록_수정_조회_성공_등록_조회_성공() {
			//when
			final GetDetailedProfileResult result = profileService.getByProfileId(profileId, profileId);

			//then
			assertThat(result.getProfileId()).isEqualTo(profileId);
			assertThat(result.getUsername()).isEqualTo("haha");
			assertThat(result.getImage()).isEqualTo(null);
			assertThat(result.getBio()).isEqualTo(null);
			assertThat(result.getFollowerCount()).isEqualTo(0);
			assertThat(result.getFolloweeCount()).isEqualTo(0);
			assertThat(result.getFavoriteCategories()).containsExactly(IT);
		}

		@Test
		void 프로필_등록_수정_조회_성공_조회_팔로워_팔로이_카운트의_관점에서() {
			//given
			final Profile profile1 = 사용자_프로필_동시_저장_등록("hoho@gmail.com", GOOGLE, "hoho", IT);
			final Profile profile2 = 사용자_프로필_동시_저장_등록("papa@gmail.com", GOOGLE, "papa", IT);

			final long profileId1 = profile1.getId();
			final long profileId2 = profile2.getId();

			팔로우_저장(profile1, profile2);

			//when
			GetDetailedProfileResult user1ToUser1ProfileResult = profileService.getByProfileId(profileId1, profileId1);
			GetDetailedProfileResult user1ToUser2ProfileResult = profileService.getByProfileId(profileId1, profileId2);
			GetDetailedProfileResult user2ToUser1ProfileResult = profileService.getByProfileId(profileId2, profileId1);
			GetDetailedProfileResult user2ToUser2ProfileResult = profileService.getByProfileId(profileId2, profileId2);

			//then
			assertDetailProfileResult(user1ToUser1ProfileResult, 0, 1, false);
			assertDetailProfileResult(user1ToUser2ProfileResult, 1, 0, true);
			assertDetailProfileResult(user2ToUser1ProfileResult, 0, 1, false);
			assertDetailProfileResult(user2ToUser2ProfileResult, 1, 0, false);
		}

		@Test
		void 프로필_등록_수정_조회_성공_등록_수정_조회() {
			//given
			final UpdateProfileCommand updateCommand = new UpdateProfileCommand(
				profileId, "papa", "updated image url", "updated bio", List.of(HUMANITIES, SCIENCE)
			);

			//when
			profileService.updateProfile(updateCommand);

			//then
			final GetDetailedProfileResult result = profileService.getByProfileId(profileId, profileId);
			assertThat(result.getUsername()).isEqualTo("papa");
			assertThat(result.getImage()).isEqualTo("updated image url");
			assertThat(result.getBio()).isEqualTo("updated bio");
			assertThat(result.getFavoriteCategories()).containsExactly(HUMANITIES, SCIENCE);
		}

		@Test
		void 프로필_등록_수정_조회_실패_사용자_이름_중복_등록() {
			//given
			long userId1 = 사용자_저장("user1@gmail.com", GOOGLE).getId();
			long userId2 = 사용자_저장("user2@gmail.com", GOOGLE).getId();

			final String username = "duplicated";
			final RegisterProfileCommand command1 = new RegisterProfileCommand(userId1, username, List.of(IT));
			final RegisterProfileCommand command2 = new RegisterProfileCommand(userId2, username, List.of(IT));

			profileService.registerProfile(command1);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profileService.registerProfile(command2));
		}

		private void assertDetailProfileResult(
			final GetDetailedProfileResult user1ToUser1ProfileResult,
			final int expectedFollowerCount,
			final int expectedFolloweeCount,
			final boolean expectedFollow
		) {
			assertThat(user1ToUser1ProfileResult.getFollowerCount()).isEqualTo(expectedFollowerCount);
			assertThat(user1ToUser1ProfileResult.getFolloweeCount()).isEqualTo(expectedFolloweeCount);
			assertThat(user1ToUser1ProfileResult.isFollow()).isEqualTo(expectedFollow);
		}
	}

	@Nested
	class 프로필_목록_조회_테스트 {

		private long profileId1;
		private long profileId2;
		private long profileId3;
		private Pageable pageable;

		@BeforeEach
		void setUp() {
			// set up 3 users
			profileId1 = 사용자_프로필_동시_등록("user1@gmail.com", GOOGLE, "user1", IT);
			profileId2 = 사용자_프로필_동시_등록("user2@naver.com", NAVER, "user2", IT);
			profileId3 = 사용자_프로필_동시_등록("user3@kakao.com", KAKAO, "user3", IT);

			pageable = createPageable();
		}

		/**
		 * user1 -> profile2, profile3 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		@Test
		void 팔로워_목록_조회_성공() {
			//given
			팔로우(profileId1, profileId3);
			팔로우(profileId1, profileId2);
			팔로우(profileId2, profileId3);
			팔로우(profileId3, profileId2);

			final ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).follower(true).build();
			final ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).follower(true).build();
			final ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).follower(true).build();

			//when
			final Slice<GetProfilesResult> result1 = profileService.getProfiles(profileId1, cond1, pageable);
			final Slice<GetProfilesResult> result2 = profileService.getProfiles(profileId2, cond2, pageable);
			final Slice<GetProfilesResult> result3 = profileService.getProfiles(profileId3, cond3, pageable);

			//then
			assertThat(result1).isEmpty();

			assertThat(result2).hasSize(2);
			assertThat(result2.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(result2.getContent().get(0).isFollow()).isFalse();
			assertThat(result2.getContent().get(1).getProfileId()).isEqualTo(profileId3);
			assertThat(result2.getContent().get(1).isFollow()).isTrue();

			assertThat(result3).hasSize(2);
			assertThat(result3.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(result3.getContent().get(0).isFollow()).isFalse();
			assertThat(result3.getContent().get(1).getProfileId()).isEqualTo(profileId2);
			assertThat(result3.getContent().get(1).isFollow()).isTrue();
		}

		/**
		 * user1 -> profile1, profile2 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		@Test
		void 팔로이_목록_조회_성공() {
			//given
			팔로우(profileId1, profileId2);
			팔로우(profileId1, profileId3);
			팔로우(profileId2, profileId3);
			팔로우(profileId3, profileId2);

			final ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).followee(true).build();
			final ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).followee(true).build();
			final ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).followee(true).build();

			//when
			final Slice<GetProfilesResult> result1 = profileService.getProfiles(profileId1, cond1, pageable);
			final Slice<GetProfilesResult> result2 = profileService.getProfiles(profileId2, cond2, pageable);
			final Slice<GetProfilesResult> result3 = profileService.getProfiles(profileId3, cond3, pageable);

			//then
			assertThat(result1).hasSize(2);
			assertThat(result1.getContent().get(0).getProfileId()).isEqualTo(profileId2);
			assertThat(result1.getContent().get(0).isFollow()).isTrue();
			assertThat(result1.getContent().get(1).getProfileId()).isEqualTo(profileId3);
			assertThat(result1.getContent().get(1).isFollow()).isTrue();

			assertThat(result2).hasSize(1);
			assertThat(result2.getContent().get(0).getProfileId()).isEqualTo(profileId3);
			assertThat(result2.getContent().get(0).isFollow()).isTrue();

			assertThat(result3).hasSize(1);
			assertThat(result3.getContent().get(0).getProfileId()).isEqualTo(profileId2);
			assertThat(result3.getContent().get(0).isFollow()).isTrue();
		}

		@Test
		void 프로필_목록_조회_이름으로_필터링_성공() {
			//given
			팔로우(profileId1, profileId2);
			final ProfileFindCond cond = ProfileFindCond.builder().username("user").build();

			//when
			final Slice<GetProfilesResult> results = profileService.getProfiles(profileId1, cond, createPageable());

			//then
			assertThat(results).hasSize(3);
			assertThat(results.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(results.getContent().get(0).isFollow()).isFalse();
			assertThat(results.getContent().get(1).getProfileId()).isEqualTo(profileId2);
			assertThat(results.getContent().get(1).isFollow()).isTrue();
			assertThat(results.getContent().get(2).getProfileId()).isEqualTo(profileId3);
			assertThat(results.getContent().get(2).isFollow()).isFalse();
		}
	}
}
