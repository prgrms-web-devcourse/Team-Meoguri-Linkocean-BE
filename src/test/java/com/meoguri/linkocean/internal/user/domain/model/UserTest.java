package com.meoguri.linkocean.internal.user.domain.model;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.test.support.internal.entity.BaseEntityTest;

class UserTest extends BaseEntityTest {

	@Test
	void 사용자_생성_성공() {
		//given
		final Email email = new Email("haha@gmail.com");
		final OAuthType oAuthType = GOOGLE;

		//when
		final User user = new User(email, oAuthType);

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOauthType)
			.containsExactly(email, oAuthType);
	}

}
