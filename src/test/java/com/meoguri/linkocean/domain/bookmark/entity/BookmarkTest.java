package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

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
		final String openType = "all";
		final String category = "it";

		//when
		final Bookmark bookmark = Bookmark.builder()
			.profile(profile)
			.title(title)
			.linkMetadata(linkMetadata)
			.memo(memo)
			.category(category)
			.openType(openType)
			.build();

		//then
		assertThat(bookmark).isNotNull()
			.extracting(
				Bookmark::getProfile,
				Bookmark::getTitle,
				Bookmark::getLinkMetadata,
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(profile, title, linkMetadata, memo, category, openType);

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
		final String category = "it";
		final String openType = "all";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Bookmark.builder()
				.profile(profile)
				.title(title)
				.linkMetadata(linkMetadata)
				.memo(memo)
				.category(category)
				.openType(openType)
				.build());
	}

	@Test
	void 북마크_업데이트_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final String updatedTitle = "updatedTitle";
		final String updatedMemo = "updatedMemo";
		final String category = "it";
		final String openType = "partial";
		final List<Tag> tags = List.of(new Tag("tag1"), new Tag("tag2"));

		//when
		bookmark.update(updatedTitle, updatedMemo, category, openType, tags);

		//then
		assertThat(bookmark)
			.extracting(
				Bookmark::getTitle,
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(updatedTitle, updatedMemo, category, openType);
		assertThat(bookmark.getTagNames())
			.containsExactly(tags.get(0).getName(), tags.get(1).getName());
	}

	@Test
	void 제목의_길이가_조건에_따른_북마크_업데이트_실패() {
		//given
		final Bookmark bookmark = createBookmark();
		final String invalidTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);
		final String updatedMemo = "updatedMemo";
		final String category = "it";
		final String openType = "partial";
		final List<Tag> tags = List.of(new Tag("tag1"), new Tag("tag2"));

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> bookmark.update(invalidTitle, updatedMemo, category, openType, tags));
	}

	@Test
	void 북마크_태그_추가_성공() {
		//given
		final Bookmark bookmark = createBookmark();

		final Tag tag = createTag();

		//when
		bookmark.addBookmarkTag(tag);

		//then
		assertThat(bookmark.getBookmarkTags()).hasSize(1);
	}
}
