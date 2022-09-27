package com.meoguri.linkocean.internal.user.application;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.test.support.internal.service.BaseServiceTest;

class OAuthAuthenticationServiceTest extends BaseServiceTest {

	@Autowired
	private OAuthAuthenticationService oAuthAuthenticationService;

	@MockBean
	private OAuthClient oAuthClient;

	@Test
	void 사용자_인증_성공() {
		//given
		final String authorizationCode = "code";
		final String redirectUri = "http://localhost/redirectUri";

		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@google.com"));

		//when
		final String jwt = oAuthAuthenticationService.authenticate(GOOGLE, authorizationCode, redirectUri);

		//then
		assertThat(jwt).isNotBlank();
	}
}
