package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

class ProfileTest {

	@Nested
	class 프로필_생성 {

		@Test
		void 프로필_생성_성공() {
			//given
			final String username = "haha";
			final List<Category> favoriteCategories = List.of(IT, ART);

			//when
			final Profile profile = new Profile(username, favoriteCategories);

			//then
			assertThat(profile.getUsername()).isEqualTo("haha");
			assertThat(profile.getFavoriteCategories()).containsExactly(IT, ART);
		}

		@ParameterizedTest
		@NullAndEmptySource
		void 프로필_생성_실패_사용자_이름이_공백(final String username) {
			//given
			final List<Category> favoriteCategories = List.of(IT, ART);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> new Profile(username, favoriteCategories));
		}

		@Test
		void 프로필_생성_실패_사용자_이름이_너무_김() {
			//given
			final String username = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);
			final List<Category> favoriteCategories = List.of(IT, ART);

			//then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> new Profile(username, favoriteCategories));
		}
	}

	@Nested
	class 프로필_업데이트 {

		@Test
		void 프로필_업데이트_성공() {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));
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
		void 프로필_업데이트_실패_사용자_이름이_공백인_경우(final String username) {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profile.update(username, "bio", "imageUrl", List.of(IT)));
		}

		@Test
		void 프로필_업데이트_실패_사용자_이름_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));
			final String tooLongUsername = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profile.update(tooLongUsername, "bio", "imageUrl", List.of(IT)));
		}

		@Test
		void 프로필_업데이트_실패_프로필_메시지_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));
			final String tooLongBio = RandomString.make(MAX_PROFILE_BIO_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profile.update("username", tooLongBio, "imageUrl", List.of(IT)));
		}

		@Test
		void 프로필_업데이트_실패_프로필_사진_주소_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));
			final String tooLongImageUrl = RandomString.make(MAX_PROFILE_IMAGE_URL_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> profile.update("username", "bio", tooLongImageUrl, List.of(IT)));
		}

		// TODO - uncomment below after remove all deprecated constructor @ Profile
		//@Test
		void 프로필_업데이트_실패_카테고리를_주지_않음() {
			//given
			final Profile profile = new Profile("haha", List.of(IT, ART));
			final List<Category> emptyCategoryList = emptyList();

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> profile.update("username", "bio", "image.png", emptyCategoryList));
		}
	}

}
