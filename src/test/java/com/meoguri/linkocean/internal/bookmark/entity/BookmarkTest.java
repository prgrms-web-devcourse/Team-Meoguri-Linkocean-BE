package com.meoguri.linkocean.internal.bookmark.entity;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.internal.profile.entity.Profile.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.internal.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.internal.tag.entity.Tag;
import com.meoguri.linkocean.test.support.internal.entity.BaseEntityTest;

class BookmarkTest extends BaseEntityTest {

	@ParameterizedTest
	@CsvSource(
		value = {"null, null", "null, title", "memo, null", "memo, title"},
		nullValues = {"null"}
	)
	void 북마크_생성_성공_제목과_메모는_null_가능(final String memo, final String title) {
		//given
		final Profile profile = createProfile();
		final Long linkMetadataId = createLinkMetadata().getId();
		final OpenType openType = ALL;
		final Category category = IT;
		final String url = "www.naver.com";

		//when
		final Bookmark bookmark =
			new Bookmark(profile, linkMetadataId, title, memo, openType, category, url, createTagIds());

		//then
		assertThat(bookmark).isNotNull()
			.extracting(
				Bookmark::getWriter,
				Bookmark::getTitle,
				b -> b.getLinkMetadataId().orElse(null),
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(profile, title, linkMetadataId, memo, category, openType);

		assertThat(bookmark)
			.extracting(Bookmark::getCreatedAt, Bookmark::getUpdatedAt)
			.doesNotContainNull();
	}

	@Test
	void 북마크_생성_성공_링크_메타데이터가_없어도_된다() {
		//given
		final Long nullLinkMetadataId = null;

		//when
		final Bookmark newBookmark = new Bookmark(
			createProfile(),
			nullLinkMetadataId,
			"title", "memo", ALL, IT, "https://hello.co.kr",
			createTagIds());

		//then
		assertThat(newBookmark).isNotNull();
		assertThat(newBookmark.getLinkMetadataId()).isEmpty();
	}

	@Test
	void 북마크_생성_실패_제목이_너무_김() {
		//given
		final String tooLongTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Bookmark(createProfile(), createLinkMetadata().getId(),
				tooLongTitle, "memo", ALL, IT, "www.google.com", createTagIds()));
	}

	@Test
	void 북마크_업데이트_성공() {
		//given
		final Bookmark bookmark = createBookmarkWithLinkMetaData();
		final String updatedTitle = "updatedTitle";
		final String updatedMemo = "updatedMemo";
		final Category category = HUMANITIES;
		final OpenType openType = PRIVATE;

		final Tag tag1 = new Tag("tag1");
		final Tag tag2 = new Tag("tag2");

		ReflectionTestUtils.setField(tag1, "id", 1L);
		ReflectionTestUtils.setField(tag2, "id", 2L);

		final TagIds tagIds = new TagIds(List.of(1L, 2L));

		//when
		bookmark.update(updatedTitle, updatedMemo, category, openType, tagIds);

		//then
		assertThat(bookmark)
			.extracting(
				Bookmark::getTitle,
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(updatedTitle, updatedMemo, category, openType);
		assertThat(bookmark.getTagIds())
			.containsExactly(1L, 2L);
	}

	@Test
	void 북마크_업데이트_실패_제목이_너무_김() {
		//given
		final String tooLongTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException().isThrownBy(
			() -> createBookmarkWithLinkMetaData().update(tooLongTitle, "updatedMemo", HUMANITIES, PRIVATE,
				createTagIds()));
	}

}
