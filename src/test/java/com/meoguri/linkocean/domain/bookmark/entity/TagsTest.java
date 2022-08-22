package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

class TagsTest {

	@Test
	void 북마크_업데이트_실패_태그_개수_초과() {
		//given
		final Tags tooManyTags = new Tags(List.of(
			new Tag("tag1"),
			new Tag("tag2"),
			new Tag("tag3"),
			new Tag("tag4"),
			new Tag("tag5"),
			new Tag("tag6")
		));

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> createBookmark()
				.update("updatedTitle", "updatedMemo", Category.HUMANITIES, OpenType.PRIVATE, tooManyTags));
	}
}