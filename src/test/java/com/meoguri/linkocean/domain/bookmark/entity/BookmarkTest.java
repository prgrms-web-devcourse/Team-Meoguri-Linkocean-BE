package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;
import com.meoguri.linkocean.domain.profile.entity.Profile;

class BookmarkTest {

	@ParameterizedTest
	@CsvSource(
		value = {"null, null", "null, title", "memo, null", "memo, title"},
		nullValues = {"null"}
	)
	void 북마크_생성_성공(final String memo, final String title) {
		//given
		final Profile profile = createProfile();
		final String textUrl = "https://www.linkocean.com";

		//when
		final Bookmark bookmark = Bookmark.builder()
			.profile(profile)
			.title(title)
			.url(textUrl)
			.memo(memo)
			.openType(ALL)
			.build();

		//then
		assertThat(bookmark).isNotNull()
			.extracting(
				Bookmark::getProfile,
				Bookmark::getTitle,
				Bookmark::getUrl,
				Bookmark::getMemo,
				Bookmark::getOpenType
			).containsExactly(profile, title, new Url(textUrl), memo, ALL);

		assertThat(bookmark)
			.extracting(
				Bookmark::getCreatedAt,
				Bookmark::getUpdatedAt
			).doesNotContainNull();
	}

	@Test
	void 제목의_길이가_조건에_따른_북마크_생성_실패() {
		//given
		final Profile profile = createProfile();
		final String title = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);
		final String textUrl = "https://www.linkocean.com";
		final String memo = "memo";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Bookmark.builder()
				.profile(profile)
				.title(title)
				.url(textUrl)
				.memo(memo)
				.openType(ALL)
				.build());
	}

	@Test
	void 북마크_업데이트_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final String updatedTitle = "updatedTitle";
		final String updatedMemo = "updatedMemo";

		//when
		bookmark.update(updatedTitle, updatedMemo, PARTIAL);

		//then
		assertThat(bookmark)
			.extracting(
				Bookmark::getTitle,
				Bookmark::getMemo,
				Bookmark::getOpenType
			).containsExactly(updatedTitle, updatedMemo, PARTIAL);
	}

	@Test
	void 제목의_길이가_조건에_따른_북마크_업데이트_실패() {
		//given
		final Bookmark bookmark = createBookmark();
		final String invalidTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);
		final String updatedMemo = "updatedMemo";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> bookmark.update(invalidTitle, updatedMemo, ALL));
	}
}
