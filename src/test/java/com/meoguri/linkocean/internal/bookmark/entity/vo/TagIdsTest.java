package com.meoguri.linkocean.internal.bookmark.entity.vo;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.TagIds.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.test.support.internal.entity.BaseEntityTest;

class TagIdsTest extends BaseEntityTest {

	@Test
	void 북마크_업데이트_실패_태그_개수_초과() {
		//given
		final List<Long> tooManyTags = new ArrayList<>();
		for (long i = 0; i < MAX_TAGS_COUNT + 1; i++) {
			tooManyTags.add(i);
		}

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> new TagIds(tooManyTags));
	}
}
