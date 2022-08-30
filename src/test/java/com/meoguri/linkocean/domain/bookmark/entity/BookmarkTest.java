package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.profile.command.entity.Profile.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.tag.entity.Tag;
import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class BookmarkTest extends BaseEntityTest {

	@ParameterizedTest
	@CsvSource(
		value = {"null, null", "null, title", "memo, null", "memo, title"},
		nullValues = {"null"}
	)
	void 북마크_생성_성공(final String memo, final String title) {
		//given
		final Profile profile = createProfile();
		final LinkMetadata linkMetadata = createLinkMetadata();
		final OpenType openType = ALL;
		final Category category = IT;
		final String url = "www.naver.com";

		//when
		final Bookmark bookmark =
			new Bookmark(profile, linkMetadata, title, memo, openType, category, url, createTagIds());

		//then
		assertThat(bookmark).isNotNull()
			.extracting(
				Bookmark::getWriter,
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
	void 북마크_생성_실패_제목이_너무_김() {
		//given
		final String tooLongTitle = RandomString.make(MAX_PROFILE_USERNAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Bookmark(createProfile(), createLinkMetadata(),
				tooLongTitle, "memo", ALL, IT, "www.google.com", createTagIds()));
	}

	@Test
	void 북마크_업데이트_성공() {
		//given
		final Bookmark bookmark = createBookmark();
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
			() -> createBookmark().update(tooLongTitle, "updatedMemo", HUMANITIES, PRIVATE, createTagIds()));
	}

}
