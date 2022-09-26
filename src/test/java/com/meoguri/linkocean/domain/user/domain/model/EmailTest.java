package com.meoguri.linkocean.domain.user.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class EmailTest extends BaseEntityTest {

	@Test
	void 이메일_생성_성공() {
		//given
		final String value = "haha@papa.com";

		//when
		final Email email = new Email(value);

		//then
		assertThat(email).isEqualTo(new Email(value));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"haha@@papa.com",
		"hahapapacom",
		"invalid@Email"
	})
	void 이메일_형식이_잘못되면_생성_실패(final String email) {
		//then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Email(email));
	}
}
