package com.meoguri.linkocean.domain.tag.entity;

import static com.meoguri.linkocean.domain.tag.entity.Tag.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.bytebuddy.utility.RandomString;

import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class TagTest extends BaseEntityTest {

	@Test
	void 태그_생성_성공() {
		//given
		final String name = "tag-name";

		//when
		final Tag tag = new Tag(name);

		//then
		assertThat(tag.getName()).isEqualTo(name);
	}

	@Test
	void 길이_제한에_맞지않는_태그_생성_실패() {
		//given
		final String invalidTag = RandomString.make(MAX_TAG_NAME_LENGTH + 1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Tag(invalidTag));
	}
}
