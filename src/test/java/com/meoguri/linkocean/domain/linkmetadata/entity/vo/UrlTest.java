package com.meoguri.linkocean.domain.linkmetadata.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UrlTest {

	@ParameterizedTest
	@ValueSource(strings = {"http://www.naver.com", "www.naver.com"})
	void url_생성_성공(final String textUrl) {
		//when
		final Url url = new Url(textUrl);

		//then
		assertThat(url).isEqualTo(new Url(textUrl));
	}

	@Test
	void url_형식이_잘못되면_생성_실패() {
		//given
		final String invalidUrl = "invalidUrl";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Url(invalidUrl));
	}
}
