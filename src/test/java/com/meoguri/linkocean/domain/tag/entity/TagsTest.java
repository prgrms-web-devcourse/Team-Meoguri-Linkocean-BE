package com.meoguri.linkocean.domain.tag.entity;

import static com.meoguri.linkocean.domain.tag.entity.Tags.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class TagsTest extends BaseEntityTest {

	@Test
	void 북마크_업데이트_실패_태그_개수_초과() {
		//given
		final List<Tag> tooManyTags = new ArrayList<>();
		for (int i = 0; i < MAX_TAGS_COUNT + 1; i++) {
			tooManyTags.add(new Tag("tag" + i));
		}

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> new Tags(tooManyTags));
	}
}
