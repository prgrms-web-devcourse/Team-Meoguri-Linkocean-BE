package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Transactional
@SpringBootTest
class ProfileServiceImplTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private FollowService followService;

	@Nested
	class 프로필_등록_수정_조회_테스트 {

		private Profile profile;

		private List<Category> categories;

		@BeforeEach
		void setUp() {
			User user = userRepository.save(createUser());

			profile = createProfile(user);

			categories = List.of(HUMANITIES, POLITICS);
		}

		@Test
		void 프로필_등록하고_조회_성공() {
			//given
			final RegisterProfileCommand command = registerCommandOf(profile, categories);
			final long profileId = profileService.registerProfile(command);

			em.flush();
			em.clear();

			//when
			final GetDetailedProfileResult result = profileService.getByProfileId(profileId, profileId);

			//then
			assertThat(result).extracting(
				GetDetailedProfileResult::getProfileId,
				GetDetailedProfileResult::getUsername,
				GetDetailedProfileResult::getImage,
				GetDetailedProfileResult::getBio,
				GetDetailedProfileResult::getFollowerCount,
				GetDetailedProfileResult::getFolloweeCount
			).containsExactly(
				profileId,
				profile.getUsername(),
				profile.getImage(),
				profile.getBio(),
				0,
				0
			);

			final List<Category> favoriteCategories = result.getFavoriteCategories();
			assertThat(favoriteCategories).containsExactly(HUMANITIES, POLITICS);
		}

		@Test
		void 프로필_등록하고_수정하고_조회_성공() {
			//given
			final RegisterProfileCommand registerCommand = registerCommandOf(profile, categories);
			final long profileId = profileService.registerProfile(registerCommand);

			em.flush();
			em.clear();

			//when
			final UpdateProfileCommand updateCommand =
				new UpdateProfileCommand(profileId, "papa", "updated image url", "updated bio",
					List.of(HUMANITIES, SCIENCE));
			profileService.updateProfile(updateCommand);

			em.flush();
			em.clear();

			//then
			final GetDetailedProfileResult result = profileService.getByProfileId(profileId, profileId);
			assertThat(result)
				.extracting(GetDetailedProfileResult::getUsername, GetDetailedProfileResult::getImage,
					GetDetailedProfileResult::getBio)
				.containsExactly("papa", "updated image url", "updated bio");

			final List<Category> favoriteCategories = result.getFavoriteCategories();
			assertThat(favoriteCategories).containsExactly(HUMANITIES, SCIENCE);
		}

		@Test
		void 사용자_이름_중복_등록_실패() {
			//given
			final User user1 = userRepository.save(createUser("haha@gmail.com", GOOGLE));
			final User user2 = userRepository.save(createUser("papa@gmail.com", GOOGLE));

			final String username = "duplicated";
			final RegisterProfileCommand command1 = new RegisterProfileCommand(user1.getId(), username, emptyList());
			final RegisterProfileCommand command2 = new RegisterProfileCommand(user2.getId(), username, emptyList());

			profileService.registerProfile(command1);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profileService.registerProfile(command2));
		}
	}

	@Test
	void 프로필_상세_조회_성공() {
		//given
		final User user1 = userRepository.save(createUser("user1@gamil.com", GOOGLE));
		final User user2 = userRepository.save(createUser("user2@gamil.com", GOOGLE));

		final long profileId1 = profileService.registerProfile(registerCommandOf(createProfile(user1, "user1")));
		final long profileId2 = profileService.registerProfile(registerCommandOf(createProfile(user2, "user2")));

		followService.follow(profileId1, profileId2);

		//when
		GetDetailedProfileResult user1ToUser1ProfileResult = profileService.getByProfileId(user1.getId(), profileId1);
		GetDetailedProfileResult user1ToUser2ProfileResult = profileService.getByProfileId(user1.getId(), profileId2);
		GetDetailedProfileResult user2ToUser1ProfileResult = profileService.getByProfileId(user2.getId(), profileId1);
		GetDetailedProfileResult user2ToUser2ProfileResult = profileService.getByProfileId(user2.getId(), profileId2);

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

	@Nested
	class 프로필_목록_조회_테스트 {
		private long user1Id;
		private long user2Id;
		private long user3Id;

		private long profile1Id;
		private long profile2Id;
		private long profile3Id;

		@BeforeEach
		void setUp() {
			// set up 3 users
			final User user1 = userRepository.save(createUser("user1@gmail.com", GOOGLE));
			final User user2 = userRepository.save(createUser("user2@naver.com", NAVER));
			final User user3 = userRepository.save(createUser("user3@kakao.com", KAKAO));

			user1Id = user1.getId();
			user2Id = user2.getId();
			user3Id = user3.getId();

			final Profile profile1 = new Profile(user1, "user1");
			final Profile profile2 = new Profile(user2, "user2");
			final Profile profile3 = new Profile(user3, "user3");

			profile1Id = profileService.registerProfile(registerCommandOf(profile1));
			profile2Id = profileService.registerProfile(registerCommandOf(profile2));
			profile3Id = profileService.registerProfile(registerCommandOf(profile3));
		}

		/**
		 * user1 -> profile2, profile3 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		@Test
		void 팔로워_목록_조회_성공() {
			//given
			followService.follow(profile1Id, profile3Id);
			followService.follow(profile1Id, profile2Id);
			followService.follow(profile2Id, profile3Id);
			followService.follow(profile3Id, profile2Id);

			//when
			final Slice<GetProfilesResult> result1 = profileService.getProfiles(user1Id,
				condWhenFindFollowers(profile1Id), createPageable());
			final Slice<GetProfilesResult> result2 = profileService.getProfiles(user2Id,
				condWhenFindFollowers(profile2Id), createPageable());
			final Slice<GetProfilesResult> result3 = profileService.getProfiles(user3Id,
				condWhenFindFollowers(profile3Id), createPageable());

			//then
			assertThat(result1).isEmpty();
			assertThat(result2)
				.extracting(
					GetProfilesResult::getProfileId,
					GetProfilesResult::getUsername,
					GetProfilesResult::getImage,
					GetProfilesResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					GetProfilesResult::getProfileId,
					GetProfilesResult::getUsername,
					GetProfilesResult::getImage,
					GetProfilesResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user2Id, "user2", null, true)
				);
		}

		/**
		 * user1 -> profile1, profile2 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		@Test
		void 팔로이_목록_조회_성공() {
			//given
			followService.follow(profile1Id, profile2Id);
			followService.follow(profile1Id, profile3Id);
			followService.follow(profile2Id, profile3Id);
			followService.follow(profile3Id, profile2Id);

			//when
			final Slice<GetProfilesResult> result1 = profileService.getProfiles(user1Id,
				condWhenFindFollowees(profile1Id), createPageable());
			final Slice<GetProfilesResult> result2 = profileService.getProfiles(user2Id,
				condWhenFindFollowees(profile2Id), createPageable());
			final Slice<GetProfilesResult> result3 = profileService.getProfiles(user3Id,
				condWhenFindFollowees(profile3Id), createPageable());

			//then
			assertThat(result1)
				.extracting(
					GetProfilesResult::getProfileId,
					GetProfilesResult::getUsername,
					GetProfilesResult::getImage,
					GetProfilesResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result2)
				.extracting(
					GetProfilesResult::getProfileId,
					GetProfilesResult::getUsername,
					GetProfilesResult::getImage,
					GetProfilesResult::isFollow
				).containsExactly(
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					GetProfilesResult::getProfileId,
					GetProfilesResult::getUsername,
					GetProfilesResult::getImage,
					GetProfilesResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true)
				);
		}

		@Test
		void 프로필_목록_조회_이름으로_필터링_성공() {
			//given
			followService.follow(profile1Id, profile2Id);

			//when
			final Slice<GetProfilesResult> results = profileService.getProfiles(user1Id,
				condWhenFindUsingUsername("user"), createPageable());

			//then
			assertThat(results)
				.extracting(GetProfilesResult::getProfileId, GetProfilesResult::isFollow)
				.containsExactly(
					tuple(user1Id, false),
					tuple(user2Id, true),
					tuple(user3Id, false)
				);
		}

		private ProfileFindCond condWhenFindUsingUsername(final String username) {
			return ProfileFindCond.builder()
				.username(username)
				.build();
		}

		private ProfileFindCond condWhenFindFollowees(final long profileId) {
			return ProfileFindCond.builder()
				.profileId(profileId)
				.followee(true)
				.build();
		}

		private ProfileFindCond condWhenFindFollowers(final long profileId) {
			return ProfileFindCond.builder()
				.profileId(profileId)
				.follower(true)
				.build();
		}
	}

	private static RegisterProfileCommand registerCommandOf(Profile profile) {
		return registerCommandOf(profile, emptyList());
	}

	private static RegisterProfileCommand registerCommandOf(Profile profile, List<Category> categories) {
		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			categories
		);
	}
}
