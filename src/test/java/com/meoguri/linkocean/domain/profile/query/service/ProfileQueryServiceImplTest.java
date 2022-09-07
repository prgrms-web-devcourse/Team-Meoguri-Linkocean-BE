package com.meoguri.linkocean.domain.profile.query.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfilesResult;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

@Transactional
class ProfileQueryServiceImplTest extends BaseServiceTest {

	@Autowired
	private ProfileQueryService profileQueryService;
	
	@Nested
	class 프로필_단건_조회 {
		private long profileId;

		@BeforeEach
		void setUp() {
			profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
		}

		@Test
		void 프로필_단건_조회_조회_성공() {
			//when
			final GetDetailedProfileResult result = profileQueryService.getByProfileId(profileId, profileId);

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
		void 프로필_단건_조회_조회_성공_팔로워_팔로이_카운트_관점에서() {
			//given
			final long profileId1 = 사용자_프로필_동시_등록("hoho@gmail.com", GOOGLE, "hoho", IT);
			final long profileId2 = 사용자_프로필_동시_등록("papa@gmail.com", GOOGLE, "papa", IT);

			팔로우(profileId1, profileId2);

			//when
			GetDetailedProfileResult user1ToUser1ProfileResult = profileQueryService.getByProfileId(profileId1,
				profileId1);
			GetDetailedProfileResult user1ToUser2ProfileResult = profileQueryService.getByProfileId(profileId1,
				profileId2);
			GetDetailedProfileResult user2ToUser1ProfileResult = profileQueryService.getByProfileId(profileId2,
				profileId1);
			GetDetailedProfileResult user2ToUser2ProfileResult = profileQueryService.getByProfileId(profileId2,
				profileId2);

			//then
			assertDetailProfileResult(user1ToUser1ProfileResult, 0, 1, false);
			assertDetailProfileResult(user1ToUser2ProfileResult, 1, 0, true);
			assertDetailProfileResult(user2ToUser1ProfileResult, 0, 1, false);
			assertDetailProfileResult(user2ToUser2ProfileResult, 1, 0, false);
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
	class 프로필_목록_조회 {

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
		void 프로필_목록_조회_팔로워_목록_조회_성공() {
			//given
			팔로우(profileId1, profileId3);
			팔로우(profileId1, profileId2);
			팔로우(profileId2, profileId3);
			팔로우(profileId3, profileId2);

			final ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).follower(true).build();
			final ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).follower(true).build();
			final ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).follower(true).build();

			//when
			final Slice<GetProfilesResult> result1 = profileQueryService.getProfiles(profileId1, cond1, pageable);
			final Slice<GetProfilesResult> result2 = profileQueryService.getProfiles(profileId2, cond2, pageable);
			final Slice<GetProfilesResult> result3 = profileQueryService.getProfiles(profileId3, cond3, pageable);

			//then
			assertThat(result1).isEmpty();

			assertThat(result2).hasSize(2);
			assertThat(result2.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(result2.getContent().get(0).isFollow()).isEqualTo(false);
			assertThat(result2.getContent().get(1).getProfileId()).isEqualTo(profileId3);
			assertThat(result2.getContent().get(1).isFollow()).isEqualTo(true);

			assertThat(result3).hasSize(2);
			assertThat(result3.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(result3.getContent().get(0).isFollow()).isEqualTo(false);
			assertThat(result3.getContent().get(1).getProfileId()).isEqualTo(profileId2);
			assertThat(result3.getContent().get(1).isFollow()).isEqualTo(true);
		}

		/**
		 * user1 -> profile1, profile2 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		@Test
		void 프로필_목록_조회_팔로이_목록_조회_성공() {
			//given
			팔로우(profileId1, profileId2);
			팔로우(profileId1, profileId3);
			팔로우(profileId2, profileId3);
			팔로우(profileId3, profileId2);

			final ProfileFindCond cond1 = ProfileFindCond.builder().profileId(profileId1).followee(true).build();
			final ProfileFindCond cond2 = ProfileFindCond.builder().profileId(profileId2).followee(true).build();
			final ProfileFindCond cond3 = ProfileFindCond.builder().profileId(profileId3).followee(true).build();

			//when
			final Slice<GetProfilesResult> result1 = profileQueryService.getProfiles(profileId1, cond1, pageable);
			final Slice<GetProfilesResult> result2 = profileQueryService.getProfiles(profileId2, cond2, pageable);
			final Slice<GetProfilesResult> result3 = profileQueryService.getProfiles(profileId3, cond3, pageable);

			//then
			assertThat(result1).hasSize(2);
			assertThat(result1.getContent().get(0).getProfileId()).isEqualTo(profileId2);
			assertThat(result1.getContent().get(0).isFollow()).isEqualTo(true);
			assertThat(result1.getContent().get(1).getProfileId()).isEqualTo(profileId3);
			assertThat(result1.getContent().get(1).isFollow()).isEqualTo(true);

			assertThat(result2).hasSize(1);
			assertThat(result2.getContent().get(0).getProfileId()).isEqualTo(profileId3);
			assertThat(result2.getContent().get(0).isFollow()).isEqualTo(true);

			assertThat(result3).hasSize(1);
			assertThat(result3.getContent().get(0).getProfileId()).isEqualTo(profileId2);
			assertThat(result3.getContent().get(0).isFollow()).isEqualTo(true);
		}

		@Test
		void 프로필_목록_조회_이름으로_필터링_성공() {
			//given
			팔로우(profileId1, profileId2);
			final ProfileFindCond cond = ProfileFindCond.builder().username("user").build();

			//when
			final Slice<GetProfilesResult> results = profileQueryService.getProfiles(profileId1, cond,
				createPageable());

			//then
			assertThat(results).hasSize(3);
			assertThat(results.getContent().get(0).getProfileId()).isEqualTo(profileId1);
			assertThat(results.getContent().get(0).isFollow()).isEqualTo(false);
			assertThat(results.getContent().get(1).getProfileId()).isEqualTo(profileId2);
			assertThat(results.getContent().get(1).isFollow()).isEqualTo(true);
			assertThat(results.getContent().get(2).getProfileId()).isEqualTo(profileId3);
			assertThat(results.getContent().get(2).isFollow()).isEqualTo(false);
		}
	}

	@Test
	void 프로필_아이디로_조회_성공() {
		//given
		final long profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);

		//when
		final Profile foundProfile = profileQueryService.findById(profileId);

		//then
		assertThat(foundProfile.getId()).isEqualTo(profileId);
	}
}
