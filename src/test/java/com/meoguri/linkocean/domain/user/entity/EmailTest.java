package com.meoguri.linkocean.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailTest {

	@Test
	void 이메일_생성_성공() {
		//given
		String value = "haha@papa.com";

		//when
		Email email = new Email(value);

		//then
		assertThat(email).isEqualTo(new Email(value));
	}
}
