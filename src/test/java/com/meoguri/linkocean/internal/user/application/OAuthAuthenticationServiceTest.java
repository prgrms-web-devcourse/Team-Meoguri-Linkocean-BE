package com.meoguri.linkocean.internal.user.application;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.meoguri.linkocean.internal.user.application.dto.AuthUserCommand;
import com.meoguri.linkocean.internal.user.application.dto.GetAuthTokenResult;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.test.support.internal.service.BaseServiceTest;

import io.jsonwebtoken.JwtException;

class OAuthAuthenticationServiceTest extends BaseServiceTest {

	@Autowired
	private OAuthAuthenticationService oAuthAuthenticationService;

	@MockBean
	private OAuthClient oAuthClient;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@Test
	void 사용자_인증_성공() {
		//given
		final AuthUserCommand command = new AuthUserCommand(GOOGLE, "code", "http://localhost/redirectUri");

		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@google.com"));

		//when
		final GetAuthTokenResult getAuthTokenResult = oAuthAuthenticationService.authenticate(command);

		//then
		assertAll(
			() -> assertThat(getAuthTokenResult.getAccessToken()).isNotBlank(),
			() -> assertThat(getAuthTokenResult.getRefreshToken()).isNotBlank()
		);
	}

	@Test
	void access_token_재발급_성공() {
		//given
		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@gmail.com"));

		final AuthUserCommand command = new AuthUserCommand(GOOGLE, "code", "http://localhost/redirectUri");
		final GetAuthTokenResult getAuthTokenResult = oAuthAuthenticationService.authenticate(command);

		//when
		final GetAuthTokenResult result = oAuthAuthenticationService.refreshAccessToken(
			getAuthTokenResult.getRefreshToken(),
			"Bearer");

		//then
		assertAll(
			() -> assertThat(result.getAccessToken()).isNotBlank(),
			() -> assertThat(result.getRefreshToken()).isNotBlank()
		);
	}

	@Test
	void access_token_재발급_실패_유효하지_않은_refresh_token() {
		//given
		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@gmail.com"));

		final AuthUserCommand command = new AuthUserCommand(GOOGLE, "code", "http://localhost/redirectUri");
		oAuthAuthenticationService.authenticate(command);

		final String invalidRefreshToken = "invalidRefreshToken";

		//when then
		assertThatExceptionOfType(JwtException.class)
			.isThrownBy(() -> oAuthAuthenticationService.refreshAccessToken(invalidRefreshToken, "Bearer"));
	}
}
