package com.meoguri.linkocean.configuration.security.oauth;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.user.entity.User;

class SessionUserTest {

	@Test
	void 세션_사용자_생성_성공() {
		//given
		final User user = createUser();

		//when
		final SessionUser sessionUser = new SessionUser(user);

		//then
		assertThat(sessionUser).isNotNull();
		assertThat(sessionUser.getId()).isEqualTo(user.getId());
	}
}
