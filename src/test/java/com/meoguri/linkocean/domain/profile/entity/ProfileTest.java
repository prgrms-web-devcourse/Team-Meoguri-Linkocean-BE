package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;

class ProfileTest {

	@Nested
	class 프로필_생성 {

		@Test
		void 프로필_생성_성공() {
			//given
			final String username = "haha";
			final FavoriteCategories favoriteCategories = new FavoriteCategories(of(IT, ART));

			//when
			final Profile profile = new Profile(username, favoriteCategories);

			//then
			assertThat(profile.getUsername()).isEqualTo("haha");
			assertThat(FavoriteCategories.toCategories(profile.getFavoriteCategories()))
				.containsExactlyInAnyOrder(IT, ART);
		}

		@ParameterizedTest
		@NullAndEmptySource
		void 프로필_생성_실패_사용자_이름이_공백(final String username) {
			//given
			final FavoriteCategories favoriteCategories = new FavoriteCategories(of(IT, ART));

			//when then
			assertThatIllegalArgumentException()
				.isThrownBy(() -> new Profile(username, favoriteCategories));
		}

		@Test
		void 프로필_생성_실패_사용자_이름이_너무_김() {
			//given
			final String username = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);
			final FavoriteCategories favoriteCategories = new FavoriteCategories(of(IT, ART));

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
			final Profile profile = new Profile("haha", new FavoriteCategories(of(IT, ART)));
			final String username = "papa";
			final String bio = "Hello world!";
			final String imageUrl = "papa.png";
			final FavoriteCategories categories = new FavoriteCategories(of(IT, HOME));

			//when
			profile.update(username, bio, imageUrl, categories);

			//then
			assertThat(profile.getUsername()).isEqualTo(username);
			assertThat(profile.getBio()).isEqualTo(bio);
			assertThat(profile.getImage()).isEqualTo(imageUrl);
			assertThat(FavoriteCategories.toCategories(profile.getFavoriteCategories()))
				.containsExactlyInAnyOrder(IT, HOME);
		}

		@ParameterizedTest
		@NullAndEmptySource
		void 프로필_업데이트_실패_사용자_이름이_공백인_경우(final String username) {
			//given
			final Profile profile = new Profile("haha", new FavoriteCategories(of(IT, HOME)));

			//when then
			assertThatIllegalArgumentException().isThrownBy(
				() -> profile.update(username, "bio", "imageUrl", new FavoriteCategories(of(IT, HOME))));
		}

		@Test
		void 프로필_업데이트_실패_사용자_이름_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", new FavoriteCategories(of(IT, HOME)));
			final String tooLongUsername = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException().isThrownBy(
				() -> profile.update(tooLongUsername, "bio", "imageUrl", new FavoriteCategories(of(IT))));
		}

		@Test
		void 프로필_업데이트_실패_프로필_메시지_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", new FavoriteCategories(of(IT, HOME)));
			final String tooLongBio = RandomString.make(MAX_PROFILE_BIO_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException().isThrownBy(
				() -> profile.update("username", tooLongBio, "imageUrl", new FavoriteCategories(of(IT))));
		}

		@Test
		void 프로필_업데이트_실패_프로필_사진_주소_길이가_너무_김() {
			//given
			final Profile profile = new Profile("haha", new FavoriteCategories(of(IT, HOME)));
			final String tooLongImageUrl = RandomString.make(MAX_PROFILE_IMAGE_URL_LENGTH + 1);

			//when then
			assertThatIllegalArgumentException().isThrownBy(
				() -> profile.update("username", "bio", tooLongImageUrl, new FavoriteCategories(of(IT, HOME))));
		}
	}

	@Nested
	class 프로필_즐겨찾기 {

		private Profile profile;
		private Bookmark bookmark1;
		private Bookmark bookmark2;

		@BeforeEach
		void setUp() {
			profile = new Profile("haha", new FavoriteCategories(of(IT, ART)));
			final LinkMetadata naver = new LinkMetadata("www.naver.com", "네이버", "naver.png");
			final LinkMetadata google = new LinkMetadata("www.google.com", "구글", "google.png");

			bookmark1 = new Bookmark(profile, naver, "bookmark1", null, ALL, null, "www.naver.com", createTags());
			bookmark2 = new Bookmark(profile, google, "bookmark2", null, ALL, null, "www.google.com", createTags());

			ReflectionTestUtils.setField(bookmark1, "id", 1L);
			ReflectionTestUtils.setField(bookmark2, "id", 2L);
		}

		@Test
		void 프로필_즐겨찾기_추가_성공() {
			//given
			profile.favorite(bookmark1);

			//when
			final boolean isFavorite1 = profile.isFavoriteBookmark(bookmark1);
			final boolean isFavorite2 = profile.isFavoriteBookmark(bookmark2);
			final List<Boolean> isFavorites = profile.isFavoriteBookmarks(of(bookmark1, bookmark2));

			//then
			assertThat(isFavorite1).isEqualTo(true);
			assertThat(isFavorite2).isEqualTo(false);
			assertThat(isFavorites).containsExactly(true, false);
		}

		@Test
		void 프로필_즐겨찾기_추가_실패() {
			//given
			profile.favorite(bookmark1);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> profile.favorite(bookmark1));
		}

		@Test
		void 프로필_즐겨찾기_취소_성공() {
			//given
			profile.favorite(bookmark1);
			assertThat(profile.isFavoriteBookmark(bookmark1)).isEqualTo(true);

			//when
			profile.unfavorite(bookmark1);

			//then
			assertThat(profile.isFavoriteBookmark(bookmark1)).isEqualTo(false);
		}

		@Test
		void 프로필_즐겨찾기_취소_실패() {
			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> profile.unfavorite(bookmark1));
		}
	}
}
