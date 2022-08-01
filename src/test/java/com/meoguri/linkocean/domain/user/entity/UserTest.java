package com.meoguri.linkocean.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.user.entity.User.OAuthType;

class UserTest {

	@Test
	void 사용자_생성_성공() {
		//given
		final String email = "haha@papa.com";
		final String oAuthType = "GOOGLE";

		//when
		final User user = new User(email, oAuthType);

		//then
		assertThat(user).isNotNull()
			.extracting(
				User::getEmail,
				User::getOAuthType
			).containsExactly(
				new Email(email),
				OAuthType.of(oAuthType)
			);
	}
}
