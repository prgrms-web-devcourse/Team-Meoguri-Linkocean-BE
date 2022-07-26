package com.meoguri.linkocean.controller.user;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.user.dto.AuthRequest;
import com.meoguri.linkocean.controller.user.dto.AuthResponse;
import com.meoguri.linkocean.controller.user.dto.RefreshAccessTokenRequest;
import com.meoguri.linkocean.exception.OAuthException;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class AuthControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(AuthController.class);

	@Test
	void 사용자_인증_api_성공() throws Exception {
		//given
		final String oAuthType = "google";
		final AuthRequest request = new AuthRequest("code", "http://localhost/redirect");

		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@google.com"));

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{oAuthType}", oAuthType)
			.contentType(APPLICATION_JSON)
			.content(createJson(request))
			.accept(APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists())
			.andExpect(jsonPath("$.tokenType").value("Bearer"))
			.andDo(print());
	}

	@Test
	void 사용자_인증_api_실패() throws Exception {
		//given
		final String oAuthType = "google";
		final AuthRequest request = new AuthRequest("code", "http://localhost/redirect");

		given(oAuthClient.getUserEmail(any())).willThrow(new OAuthException("OAuth 로그인에 실패했습니다."));

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{oAuthType}", oAuthType)
			.contentType(APPLICATION_JSON)
			.content(createJson(request))
			.accept(APPLICATION_JSON));

		//then
		perform
			.andExpect(status().is5xxServerError())
			.andDo(print());
	}

	@Test
	void access_token_재발급_성공() throws Exception {
		//given
		final AuthResponse authResponse = 로그인(GOOGLE, "code", "http://localhost/redirect");
		final RefreshAccessTokenRequest request = new RefreshAccessTokenRequest(
			authResponse.getRefreshToken(),
			authResponse.getTokenType());

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/token/refresh")
			.contentType(APPLICATION_JSON)
			.content(createJson(request))
			.accept(APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists())
			.andExpect(jsonPath("$.tokenType").value("Bearer"))
			.andDo(print());
	}

	@Test
	void access_token_재발급_실패_유효하지_않은_refresh_token() throws Exception {
		//given
		final String invalidRefreshToken = "invalidRefreshToken";
		final RefreshAccessTokenRequest request = new RefreshAccessTokenRequest(invalidRefreshToken, "Bearer");

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/token/refresh")
			.contentType(APPLICATION_JSON)
			.content(createJson(request))
			.accept(APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isUnauthorized());
	}
}
