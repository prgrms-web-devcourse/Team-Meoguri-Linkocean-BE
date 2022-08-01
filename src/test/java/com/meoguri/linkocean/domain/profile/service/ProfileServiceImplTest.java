package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
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
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;
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
				GetMyProfileResult::getImageUrl,
				GetMyProfileResult::getBio,
				GetMyProfileResult::getFollowerCount,
				GetMyProfileResult::getFolloweeCount,
				GetMyProfileResult::isFollow
			).containsExactly(
				profileId,
				profile.getUsername(),
				profile.getImageUrl(),
				profile.getBio(),
				0,
				0,
				false
			);

			assertThat(result.getCategories()).containsExactly("인문", "정치");
		}

		@Test
		void 프로필_등록하고_수정하고_조회_성공() {
			//given
			final RegisterProfileCommand registerCommand = registerCommandOf(profile, categories);
			profileService.registerProfile(registerCommand);

			em.flush();
			em.clear();

			//when
			final UpdateProfileCommand updateCommand = new UpdateProfileCommand(
				userId,
				"papa",
				"updated image url",
				"updated bio",
				List.of("인문", "과학")
			);

			profileService.updateProfile(updateCommand);

			em.flush();
			em.clear();

			//then
			final GetMyProfileResult result = profileService.getMyProfile(userId);
			assertThat(result).extracting(
				GetMyProfileResult::getUsername,
				GetMyProfileResult::getImageUrl,
				GetMyProfileResult::getBio
			).containsExactly(
				"papa",
				"updated image url",
				"updated bio"
			);

			assertThat(result.getCategories()).containsExactly("인문", "과학");
		}
	}

	@Nested
	class 프로필_목록_조회_테스트 {

		private long user1Id;
		private long user2Id;
		private long user3Id;

		private long profile1Id;
		private long profile2Id;
		private long profile3Id;

		@Autowired
		private FollowService followService;

		@BeforeEach
		void setUp() {
			// set up 3 users
			User user1 = userRepository.save(new User("user1@gmail.com", "GOOGLE"));
			User user2 = userRepository.save(new User("user2@naver.com", "NAVER"));
			User user3 = userRepository.save(new User("user3@kakao.com", "KAKAO"));

			user1Id = user1.getId();
			user2Id = user2.getId();
			user3Id = user3.getId();

			Profile profile1 = new Profile(user1, "user1");
			Profile profile2 = new Profile(user2, "user2");
			Profile profile3 = new Profile(user3, "user3");

			profile1Id = profileService.registerProfile(registerCommandOf(profile1, emptyList()));
			profile2Id = profileService.registerProfile(registerCommandOf(profile2, emptyList()));
			profile3Id = profileService.registerProfile(registerCommandOf(profile3, emptyList()));
		}

		@Test
		void 팔로워_목록_조회_성공() {
			//given
			followService.follow(new FollowCommand(user1Id, profile2Id));
			followService.follow(new FollowCommand(user1Id, profile3Id));
			followService.follow(new FollowCommand(user2Id, profile3Id));
			followService.follow(new FollowCommand(user3Id, profile2Id));

			//when
			final List<SearchProfileResult> result1
				= profileService.searchFollowerProfiles(defaultSearchCondOfUserId(user1Id));
			final List<SearchProfileResult> result2
				= profileService.searchFollowerProfiles(defaultSearchCondOfUserId(user2Id));
			final List<SearchProfileResult> result3
				= profileService.searchFollowerProfiles(defaultSearchCondOfUserId(user3Id));

			//then
			assertThat(result1).isEmpty();
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImageUrl,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user1Id, "user1", null, false),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImageUrl,
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
			final List<SearchProfileResult> result1
				= profileService.searchFolloweeProfiles(defaultSearchCondOfUserId(user1Id));
			final List<SearchProfileResult> result2
				= profileService.searchFolloweeProfiles(defaultSearchCondOfUserId(user2Id));
			final List<SearchProfileResult> result3
				= profileService.searchFolloweeProfiles(defaultSearchCondOfUserId(user3Id));

			//then
			assertThat(result1)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImageUrl,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true),
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result2)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImageUrl,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user3Id, "user3", null, true)
				);
			assertThat(result3)
				.extracting(
					SearchProfileResult::getId,
					SearchProfileResult::getUsername,
					SearchProfileResult::getImageUrl,
					SearchProfileResult::isFollow
				).containsExactly(
					tuple(user2Id, "user2", null, true)
				);
		}
	}

	public static RegisterProfileCommand registerCommandOf(Profile profile, List<String> categories) {

		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			categories
		);
	}

	static ProfileSearchCond defaultSearchCondOfUserId(long userId) {

		return new ProfileSearchCond(userId, null, null, null);
	}
}
