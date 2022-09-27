package com.meoguri.linkocean.internal.linkmetadata.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.meoguri.linkocean.test.support.internal.entity.BaseEntityTest;

class LinkTest extends BaseEntityTest {

	@ParameterizedTest
	@ValueSource(
		strings = {
			"http://www.naver.com",
			"https://www.naver.com",
			"www.naver.com",
			"naver.com",
			"dev.naver.com",
			"https://yozm.wishket.com/magazine/detail/1217",
			"https://map.naver.com/v5/entry/place/1368783268?c=14142328.7122523,4515635.3114847,13,0,0,0,dh&placePath=%2Fhome&entry=plt",
			"https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=쵸리상경"
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
