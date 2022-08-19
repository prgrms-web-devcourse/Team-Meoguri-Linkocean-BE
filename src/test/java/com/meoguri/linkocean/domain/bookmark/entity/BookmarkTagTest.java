package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookmarkTagTest {

	@Test
	void 북마크_태그_생성_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final Tag tag = createTag();

		//when
		final BookmarkTag bookmarkTag = new BookmarkTag(bookmark, tag);

		//then
		assertThat(bookmarkTag).isNotNull()
			.extracting(BookmarkTag::getBookmark, BookmarkTag::getTag)
			.containsExactly(bookmark, tag);
	}
}
