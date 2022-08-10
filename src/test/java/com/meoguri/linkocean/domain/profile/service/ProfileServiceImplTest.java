package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.common.Ultimate;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

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

		private long userId;

		private Profile profile;

		private List<String> categories;

		@BeforeEach
		void setUp() {
			User user = userRepository.save(createUser());
			userId = user.getId();

			profile = createProfile(user);
			categories = List.of("인문", "정치");
		}

		@Test
		void 프로필_등록하고_조회_성공() {
			//given
			final RegisterProfileCommand command = registerCommandOf(profile, categories);
			final long profileId = profileService.registerProfile(command);

			em.flush();
			em.clear();

			//when
			final GetMyProfileResult result = profileService.getMyProfile(userId);

			//then
			assertThat(result).extracting(
				GetMyProfileResult::getProfileId,
				GetMyProfileResult::getUsername,
				GetMyProfileResult::getImage,
				GetMyProfileResult::getBio,
				GetMyProfileResult::getFollowerCount,
				GetMyProfileResult::getFolloweeCount
			).containsExactly(
				profileId,
				profile.getUsername(),
				profile.getImage(),
				profile.getBio(),
				0,
				0
			);

			final List<String> favoriteCategories = result.getFavoriteCategories();
			assertThat(favoriteCategories).containsExactly("인문", "정치");
		}

		@Test
		void 프로필_등록하고_수정하고_조회_성공() {
			//given
			final RegisterProfileCommand registerCommand = registerCommandOf(profile, categories);
			profileService.registerProfile(registerCommand);

			em.flush();
			em.clear();

			//when
			final UpdateProfileCommand updateCommand =
				new UpdateProfileCommand(userId, "papa", "updated image url", "updated bio", List.of("인문", "과학"));
			profileService.updateProfile(updateCommand);

			em.flush();
			em.clear();

			//then
			final GetMyProfileResult result = profileService.getMyProfile(userId);
			assertThat(result)
				.extracting(GetMyProfileResult::getUsername, GetMyProfileResult::getImage, GetMyProfileResult::getBio)
				.containsExactly("papa", "updated image url", "updated bio");

			final List<String> favoriteCategories = result.getFavoriteCategories();
			assertThat(favoriteCategories).containsExactly("인문", "과학");
		}
	}

	@Test
	void 프로필_상세_조회_성공() {
		//given
		final User user1 = userRepository.save(new User("user1@gamil.com", "GOOGLE"));
		final User user2 = userRepository.save(new User("user2@gamil.com", "GOOGLE"));

		final long profileId1 = profileService.registerProfile(registerCommandOf(createProfile(user1, "user1")));
		final long profileId2 = profileService.registerProfile(registerCommandOf(createProfile(user2, "user2")));

		followService.follow(new FollowCommand(user1.getId(), profileId2));

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

	@Ultimate
	@Nested
	class 궁극의_프로필_목록_조회_테스트 {
		private long user1Id;
		private long user2Id;
		private long user3Id;

		private long profile1Id;
		private long profile2Id;
		private long profile3Id;

		@BeforeEach
		void setUp() {
			// set up 3 users
			final User user1 = userRepository.save(new User("user1@gmail.com", "GOOGLE"));
			final User user2 = userRepository.save(new User("user2@naver.com", "NAVER"));
			final User user3 = userRepository.save(new User("user3@kakao.com", "KAKAO"));

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
		 * user1 -> profile1, profile2 팔로우
		 * user2 -> profile3 팔로우
		 * user3 -> profile2 팔로우
		 */
		private void makeScenario() {
			followService.follow(new FollowCommand(user1Id, profile2Id));
			followService.follow(new FollowCommand(user1Id, profile3Id));
			followService.follow(new FollowCommand(user2Id, profile3Id));
			followService.follow(new FollowCommand(user3Id, profile2Id));
		}

		@Test
		void 팔로워_목록_조회_성공() {
			//given
			makeScenario();

			//when
			final Page<SearchProfileResult> result1 = profileService.getProfiles(user1Id,
				condWhenFindFollowers(profile1Id), defaultPageable());
			final Page<SearchProfileResult> result2 = profileService.getProfiles(user2Id,
				condWhenFindFollowers(profile2Id), defaultPageable());
			final Page<SearchProfileResult> result3 = profileService.getProfiles(user3Id,
				condWhenFindFollowers(profile3Id), defaultPageable());

			//then
			assertThat(result1).isEmpty();
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user2Id, "user2", null, true)
				);
		}

		@Test
		void 팔로이_목록_조회_성공() {
			//given
			makeScenario();

			//when
			final Page<SearchProfileResult> result1 = profileService.getProfiles(user1Id,
				condWhenFindFollowees(profile1Id), defaultPageable());
			final Page<SearchProfileResult> result2 = profileService.getProfiles(user2Id,
				condWhenFindFollowees(profile2Id), defaultPageable());
			final Page<SearchProfileResult> result3 = profileService.getProfiles(user3Id,
				condWhenFindFollowees(profile3Id), defaultPageable());

			//then
			assertThat(result1)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true)
				);
		}

		@Test
		void 프로필_목록_조회_이름으로_필터링_성공() {
			//given
			followService.follow(new FollowCommand(user1Id, profile2Id));

			//when
			final Page<SearchProfileResult> results = profileService.getProfiles(user1Id,
				condWhenFindUsingUsername("user"), defaultPageable());

			//then
			assertThat(results)
				.extracting(SearchProfileResult::getId, SearchProfileResult::isFollow)
				.containsExactly(
					tuple(user1Id, false),
					tuple(user2Id, true),
					tuple(user3Id, false)
				);
		}

		private UltimateProfileFindCond condWhenFindUsingUsername(final String username) {
			return UltimateProfileFindCond.builder()
				.username(username)
				.build();
		}

		private UltimateProfileFindCond condWhenFindFollowees(final long profileId) {
			return UltimateProfileFindCond.builder()
				.profileId(profileId)
				.followee(true)
				.build();
		}

		private UltimateProfileFindCond condWhenFindFollowers(final long profileId) {
			return UltimateProfileFindCond.builder()
				.profileId(profileId)
				.follower(true)
				.build();
		}
	}

	@Disabled("이전 완료(곧 삭제 예정)")
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
			final User user1 = userRepository.save(new User("user1@gmail.com", "GOOGLE"));
			final User user2 = userRepository.save(new User("user2@naver.com", "NAVER"));
			final User user3 = userRepository.save(new User("user3@kakao.com", "KAKAO"));

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

		@Test
		void 팔로워_목록_조회_성공() {
			//given
			followService.follow(new FollowCommand(user1Id, profile2Id));
			followService.follow(new FollowCommand(user1Id, profile3Id));
			followService.follow(new FollowCommand(user2Id, profile3Id));
			followService.follow(new FollowCommand(user3Id, profile2Id));

			//when
			final List<SearchProfileResult> result1 = profileService.searchFollowerProfiles(cond(user1Id), profile1Id);
			final List<SearchProfileResult> result2 = profileService.searchFollowerProfiles(cond(user2Id), profile2Id);
			final List<SearchProfileResult> result3 = profileService.searchFollowerProfiles(cond(user3Id), profile3Id);

			//then
			assertThat(result1).isEmpty();
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user2Id, "user2", null, true)
				);
		}

		@Test
		void 팔로이_목록_조회_성공() {
			//given
			followService.follow(new FollowCommand(user1Id, profile2Id));
			followService.follow(new FollowCommand(user1Id, profile3Id));
			followService.follow(new FollowCommand(user2Id, profile3Id));
			followService.follow(new FollowCommand(user3Id, profile2Id));

			//when
			final List<SearchProfileResult> result1 = profileService.searchFolloweeProfiles(cond(user1Id), profile1Id);
			final List<SearchProfileResult> result2 = profileService.searchFolloweeProfiles(cond(user2Id), profile2Id);
			final List<SearchProfileResult> result3 = profileService.searchFolloweeProfiles(cond(user3Id), profile3Id);

			//then
			assertThat(result1)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImage,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true)
				);
		}

		@Test
		void 프로필_목록_조회_이름으로_필터링_성공() {
			//given
			followService.follow(new FollowCommand(user1Id, profile2Id));

			//when
			final List<SearchProfileResult> results = profileService.searchProfilesByUsername(cond(user1Id, "user"));

			//then
			assertThat(results)
				.extracting(SearchProfileResult::getId, SearchProfileResult::isFollow)
				.containsExactly(
					tuple(user1Id, false),
					tuple(user2Id, true),
					tuple(user3Id, false)
				);
		}
	}

	private static RegisterProfileCommand registerCommandOf(Profile profile) {
		return registerCommandOf(profile, emptyList());
	}

	private static RegisterProfileCommand registerCommandOf(Profile profile, List<String> categories) {
		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			categories
		);
	}

	private static ProfileSearchCond cond(long userId) {
		return cond(userId, null);
	}

	private static ProfileSearchCond cond(long userId, String username) {
		return new ProfileSearchCond(userId, null, null, username);
	}
}
