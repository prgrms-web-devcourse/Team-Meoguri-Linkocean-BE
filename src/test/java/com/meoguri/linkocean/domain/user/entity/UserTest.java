package com.meoguri.linkocean.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void 사용자_생성_성공() {
		//given
		Email email = new Email("haha@papa.com");
		OAuthType oAuthType = OAuthType.GOOGLE;

		//when
		User user = new User(email, oAuthType);

		//then
		assertThat(user).isNotNull()
			.extracting(
				User::getEmail,
				User::getOAuthType
			).containsExactly(
				email,
				oAuthType
			);
	}
}
