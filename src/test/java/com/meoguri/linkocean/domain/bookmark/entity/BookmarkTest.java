package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
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
		final LinkMetadata linkMetadata = createLinkMetadata();

		//when
		final Bookmark bookmark = Bookmark.builder()
			.profile(profile)
			.title(title)
			.linkMetadata(linkMetadata)
			.memo(memo)
			.openType(ALL)
			.build();

		//then
		assertThat(bookmark).isNotNull()
			.extracting(
				Bookmark::getProfile,
				Bookmark::getTitle,
				Bookmark::getLinkMetadata,
				Bookmark::getMemo,
				Bookmark::getOpenType
			).containsExactly(profile, title, linkMetadata, memo, ALL);

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
		final LinkMetadata linkMetadata = createLinkMetadata();
		final String memo = "memo";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Bookmark.builder()
				.profile(profile)
				.title(title)
				.linkMetadata(linkMetadata)
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
