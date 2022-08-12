package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.common.Assertions.*;
import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
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
		final OpenType openType = OpenType.ALL;
		final Category category = Category.IT;
		final String url = "www.naver.com";

		//when
		final Bookmark bookmark =
			new Bookmark(profile, linkMetadata, title, memo, openType, category, url, emptyList());

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
			.extracting(Bookmark::getCreatedAt, Bookmark::getUpdatedAt)
			.doesNotContainNull();
	}

	@Test
	void 제목의_길이가_조건에_따른_북마크_생성_실패() {
		//given
		final String tooLongTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Bookmark(createProfile(), createLinkMetadata(),
				tooLongTitle, "memo", OpenType.ALL, Category.IT, "www.google.com", emptyList()));
	}

	@Test
	void 북마크_생성_태그_테스트() {
		//given
		final Tag tag1 = new Tag("tag1");
		final Tag tag2 = new Tag("tag2");

		// when
		final Bookmark bookmark = new Bookmark(
			createProfile(),
			createLinkMetadata(),
			"title",
			"memo",
			OpenType.ALL,
			Category.IT,
			"www.naver.com",
			List.of(tag1, tag2)
		);

		//then
		assertThat(bookmark.getTagNames()).hasSize(2).containsExactly("tag1", "tag2");
	}

	@Test
	void 북마크_태그_추가_실패_태그_개수_한도_초과() {
		//given
		List<Tag> tooManyTags = List.of(
			new Tag("tag1"),
			new Tag("tag2"),
			new Tag("tag3"),
			new Tag("tag4"),
			new Tag("tag5"),
			new Tag("tag6")
		);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> new Bookmark(
				createProfile(),
				createLinkMetadata(),
				"title",
				"memo",
				OpenType.ALL,
				Category.IT,
				"www.google.com",
				tooManyTags
			));
	}

	@Test
	void 북마크_업데이트_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final String updatedTitle = "updatedTitle";
		final String updatedMemo = "updatedMemo";
		final Category category = Category.HUMANITIES;
		final OpenType openType = OpenType.PRIVATE;
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
		final String invalidTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> createBookmark()
				.update(invalidTitle, "updatedMemo", Category.HUMANITIES, OpenType.PRIVATE, emptyList()));
	}

	@Test
	void 북마크_업데이트_실패_태그_개수_초과() {
		//given
		final List<Tag> tooManyTags = List.of(
			new Tag("tag1"),
			new Tag("tag2"),
			new Tag("tag3"),
			new Tag("tag4"),
			new Tag("tag5"),
			new Tag("tag6"));

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> createBookmark()
				.update("updatedTitle", "updatedMemo", Category.HUMANITIES, OpenType.PRIVATE, tooManyTags));
	}
}
