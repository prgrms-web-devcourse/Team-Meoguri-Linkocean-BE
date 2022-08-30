package com.meoguri.linkocean.domain.linkmetadata.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class LinkTest extends BaseEntityTest {

	@ParameterizedTest
	@ValueSource(
		strings = {
			"http://www.naver.com",
			"https://www.naver.com",
			"www.naver.com",
			"naver.com",
			"dev.naver.com"
		}
	)
	void url_생성_성공(final String textUrl) {
		//when
		final Link link = new Link(textUrl);

		//then
		assertThat(link).isEqualTo(new Link(textUrl));
	}

	@Test
	void url_형식이_잘못되면_생성_실패() {
		//given
		final String invalidUrl = "i do not have a dot";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Link(invalidUrl));
	}
}
