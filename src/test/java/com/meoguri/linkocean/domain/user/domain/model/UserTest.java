package com.meoguri.linkocean.domain.user.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.test.support.domain.entity.BaseEntityTest;

class UserTest extends BaseEntityTest {

	@Test
	void 사용자_생성_성공() {
		//given
		final Email email = new Email("haha@papa.com");
		final OAuthType oAuthType = OAuthType.GITHUB;

		//when
		final User user = new User(email, oAuthType);

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOauthType)
			.containsExactly(email, oAuthType);
	}

}
