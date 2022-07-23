package com.meoguri.linkocean.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void 사용자_생성_성공() {
		//given
		OAuthType oAuthType = OAuthType.GOOGLE;
		Email email = new Email("haha@papa.com");

		//when
		User user = new User(oAuthType, email);

		//then
		assertThat(user).isNotNull()
			.extracting(
				User::getOAuthType,
				User::getEmail
			).containsExactly(
				oAuthType,
				email
			);
	}
}
