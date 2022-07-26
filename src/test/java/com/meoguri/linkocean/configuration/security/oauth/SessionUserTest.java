package com.meoguri.linkocean.configuration.security.oauth;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.domain.user.entity.User;

class SessionUserTest {

	@Test
	void 세션_사용자_생성_성공() {
		//given
		final User user = createUser();
		ReflectionTestUtils.setField(user, "id", 1L); // Long -> long (null casting) 으로 인한 예외 방지

		//when
		final SessionUser sessionUser = new SessionUser(user);

		//then
		assertThat(sessionUser).isNotNull();
		assertThat(sessionUser.getId()).isEqualTo(1L);
	}
}
