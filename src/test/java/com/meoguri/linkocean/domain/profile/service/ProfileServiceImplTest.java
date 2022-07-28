package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class ProfileServiceImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileService profileService;

	private long userId;

	private Profile profile;

	private List<String> categories;

	@BeforeEach
	void setUp() {
		User user = userRepository.save(createUser());
		userId = user.getId();

		profile = createProfile(user);
		categories = List.of("it", "self_development");
	}

	@Test
	void 프로필_등록하고_조회_성공() {
		//given
		final RegisterProfileCommand command = ProfileCommand.ofRegister(profile, categories);
		final long profileId = profileService.registerProfile(command);

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

		assertThat(result.getCategories()).containsExactly("it", "self_development");
	}

	@Test
	void 프로필_등록하고_수정하고_조회_성공() {
		//given
		final RegisterProfileCommand registerCommand = ProfileCommand.ofRegister(profile, categories);
		profileService.registerProfile(registerCommand);

		final UpdateProfileCommand updateCommand = new UpdateProfileCommand(
			userId,
			"papa",
			"updated image url",
			"updated bio",
			List.of("it", "science")
		);

		//when
		profileService.updateProfile(updateCommand);

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

		assertThat(result.getCategories()).containsExactly("it", "science");
	}

	static final class ProfileCommand {

		public static RegisterProfileCommand ofRegister(Profile profile, List<String> categories) {

			return new RegisterProfileCommand(
				profile.getUser().getId(),
				profile.getUsername(),
				categories
			);
		}
	}
}
