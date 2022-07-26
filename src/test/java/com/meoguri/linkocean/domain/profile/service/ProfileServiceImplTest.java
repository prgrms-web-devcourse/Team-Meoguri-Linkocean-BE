package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.ProfileCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class ProfileServiceImplTest {

	private long userId;

	private Profile profile;

	private List<String> categories;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileService profileService;

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
		final ProfileResult result = profileService.getProfileByUserId(userId);

		//then
		assertThat(result).extracting(
			ProfileResult::getProfileId,
			ProfileResult::getUsername,
			ProfileResult::getImageUrl,
			ProfileResult::getBio,
			ProfileResult::getFollowerCount,
			ProfileResult::getFolloweeCount,
			ProfileResult::isFollow
		).containsExactly(
			profileId,
			profile.getUsername(),
			profile.getImageUrl(),
			profile.getBio(),
			0,
			0,
			false
		);

		assertThat(result.getCategories()).isNull();
		// assertThat(result.getCategories()).containsExactly("it", "self_development"); // <- 카테고리 구현 후 주석 풀기
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
		final ProfileResult result = profileService.getProfileByUserId(userId);
		assertThat(result).extracting(
			ProfileResult::getUsername,
			ProfileResult::getImageUrl,
			ProfileResult::getBio
		).containsExactly(
			"papa",
			"updated image url",
			"updated bio"
		);
		assertThat(result.getCategories()).isNull();
		// assertThat(result.getCategories()).containsExactly("it", "science"); // <- 카테고리 구현 후 주석 풀기
	}
}