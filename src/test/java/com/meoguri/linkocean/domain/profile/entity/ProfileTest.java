package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.meoguri.linkocean.domain.user.entity.User;

class ProfileTest {

	@Test
	void 프로필_생성_성공() {
		//given
		final User user = createUser();
		final String username = "haha";

		//when
		final Profile profile = new Profile(user, username);

		//then
		assertThat(profile).isNotNull()
			.extracting(Profile::getUser, Profile::getUsername)
			.containsExactly(user, username);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 사용자_이름이_공백인_경우_프로필_생성_실패(final String username) {
		//given
		final User user = createUser();

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Profile(user, username));
	}

	@Test
	void 사용자_이름_길이_조건에따른_프로필_생성_실패() {
		//given
		final User user = createUser();
		final String username = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Profile(user, username));
	}

	@Test
	void 프로필_업데이트_성공() {
		//given
		final Profile profile = createProfile();
		final String username = "papa";
		final String bio = "Hello world!";
		final String imageUrl = "papa.png";

		//when
		profile.update(username, bio, imageUrl);

		//then
		assertThat(profile).isNotNull()
			.extracting(Profile::getUsername, Profile::getBio, Profile::getImageUrl)
			.containsExactly(username, bio, imageUrl);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 사용자_이름이_공백인_경우_프로필_업데이트_실패(final String username) {
		//given
		final Profile profile = createProfile();

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update(username, "bio", "imageUrl"));
	}

	@Test
	void 사용자_이름_길이_조건에따른_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String invalidUsername = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update(invalidUsername, "bio", "imageUrl"));
	}

	@Test
	void 프로필_메시지_길이_조건에따른_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String invalidBio = RandomString.make(MAX_PROFILE_BIO_LENGTH + 1);

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update("username", invalidBio, "imageUrl"));
	}

	@Test
	void 프로필_사진_주소_길이_조건에따른_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String invalidImageUrl = RandomString.make(MAX_PROFILE_IMAGE_URL_LENGTH + 1);

		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update("username", "bio", invalidImageUrl));
	}
}
