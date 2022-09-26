package com.meoguri.linkocean.domain.user.domain;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.domain.model.OAuthType.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.command.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.domain.dto.GetUserResult;
import com.meoguri.linkocean.domain.user.domain.model.Email;
import com.meoguri.linkocean.domain.user.domain.model.OAuthType;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

class UserServiceImplTest extends BaseServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ProfileRepository profileRepository;

	@Test
	void 사용자_조회_성공() {
		//given
		사용자_없으면_등록("haha@gmail.com", GOOGLE);

		//when
		final GetUserResult result = userService.getUser(new Email("haha@gmail.com"), GOOGLE);

		//then
		assertThat(result.getId()).isNotNull();
		assertThat(result.getProfileId()).isNull();
		assertThat(result.getEmail()).isEqualTo(new Email("haha@gmail.com"));
		assertThat(result.getOauthType()).isEqualTo(GOOGLE);
	}

	@Test
	void 사용자_없으면_등록_성공() {
		//given
		final Email email = new Email("crush@github.com");
		final OAuthType oAuthType = GITHUB;

		//when
		final long userId = userService.registerIfNotExists(email, oAuthType);

		//then
		final GetUserResult result = 사용자_조회("crush@github.com", GITHUB);
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getProfileId()).isNull();
		assertThat(result.getEmail()).isEqualTo(new Email("crush@github.com"));
		assertThat(result.getOauthType()).isEqualTo(GITHUB);
	}

	@Test
	void 프로필_등록_성공() {
		//given
		final long userId = 사용자_없으면_등록("crush@github.com", GITHUB);
		final Profile profile = 프로필_저장("crush", IT, ART);

		//when
		userService.registerProfile(userId, profile);

		//then
		final GetUserResult result = 사용자_조회("crush@github.com", GITHUB);
		assertThat(result.getProfileId()).isEqualTo(profile.getId());
	}

	private Profile 프로필_저장(final String username, final Category... categories) {
		return profileRepository.save(
			new Profile(username, new FavoriteCategories(Arrays.stream(categories).collect(toList()))));
	}
}
