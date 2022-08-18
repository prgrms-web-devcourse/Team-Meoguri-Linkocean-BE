package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.common.Assertions.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
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

	@Test
	void user가_null인_경우_프로필_생성_실패() {
		//given
		User nullUser = null;

		//when then
		assertThatNullPointerException()
			.isThrownBy(() -> new Profile(nullUser, "crush"));
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
		final List<Category> categories = List.of(IT, HOME);

		//when
		profile.update(username, bio, imageUrl, categories);

		//then
		assertThat(profile.getUsername()).isEqualTo(username);
		assertThat(profile.getBio()).isEqualTo(bio);
		assertThat(profile.getImage()).isEqualTo(imageUrl);
		assertThat(profile.getFavoriteCategories()).containsExactlyElementsOf(categories);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 사용자_이름이_공백인_경우_프로필_업데이트_실패(final String username) {
		//given
		final Profile profile = createProfile();

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update(username, "bio", "imageUrl", List.of(IT)));
	}

	@Test
	void 사용자_이름_길이가_너무_길면_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String tooLongUsername = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update(tooLongUsername, "bio", "imageUrl", List.of(IT)));
	}

	@Test
	void 프로필_메시지_길이가_너무_길면_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String tooLongBio = RandomString.make(MAX_PROFILE_BIO_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update("username", tooLongBio, "imageUrl", List.of(IT)));
	}

	@Test
	void 프로필_사진_주소_길이가_너무길면_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final String tooLongImageUrl = RandomString.make(MAX_PROFILE_IMAGE_URL_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profile.update("username", "bio", tooLongImageUrl, List.of(IT)));
	}

	// TODO - uncomment below after remove all deprecated constructor @ Profile
	//@Test
	void 카테고리를_주지_않으면_프로필_업데이트_실패() {
		//given
		final Profile profile = createProfile();
		final List<Category> emptyCategoryList = emptyList();

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> profile.update("username", "bio", "image.png", emptyCategoryList));
	}
}
