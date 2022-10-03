package com.meoguri.linkocean.internal.user.domain;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.profile.command.persistence.ProfileRepository;
import com.meoguri.linkocean.internal.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.internal.user.domain.dto.GetUserResult;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;
import com.meoguri.linkocean.internal.user.domain.model.User;
import com.meoguri.linkocean.test.support.internal.service.BaseServiceTest;

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
	void 사용자_아이디로_사용자_조회_성공() {
		//given
		final long userId = 사용자_없으면_등록("crush@gmail.com", GOOGLE);

		//when
		final User user = userService.getUser(userId);

		//then
		assertThat(user).isNotNull();
	}

	@Test
	void 사용자_아이디로_사용자_조회_실패() {
		//given
		final long invalidUserId = -1L;

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> userService.getUser(invalidUserId));
	}

	@Test
	void 사용자_없으면_등록_성공() {
		//given
		final Email email = new Email("crush@gmail.com");
		final OAuthType oAuthType = GOOGLE;

		//when
		final long userId = userService.registerIfNotExists(email, oAuthType);

		//then
		final GetUserResult result = 사용자_조회("crush@gmail.com", GOOGLE);
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getProfileId()).isNull();
		assertThat(result.getEmail()).isEqualTo(new Email("crush@gmail.com"));
		assertThat(result.getOauthType()).isEqualTo(GOOGLE);
	}

	@Test
	void 프로필_등록_성공() {
		//given
		final long userId = 사용자_없으면_등록("crush@gmail.com", GOOGLE);
		final Profile profile = 프로필_저장("crush", IT, ART);

		//when
		userService.registerProfile(userId, profile);

		//then
		final GetUserResult result = 사용자_조회("crush@gmail.com", GOOGLE);
		assertThat(result.getProfileId()).isEqualTo(profile.getId());
	}

	private Profile 프로필_저장(final String username, final Category... categories) {
		return profileRepository.save(
			new Profile(username, new FavoriteCategories(Arrays.stream(categories).collect(toList()))));
	}
}
