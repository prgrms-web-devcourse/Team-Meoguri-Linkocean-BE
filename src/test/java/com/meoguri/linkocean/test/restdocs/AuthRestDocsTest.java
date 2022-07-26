package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.user.AuthController;
import com.meoguri.linkocean.controller.user.dto.AuthRequest;
import com.meoguri.linkocean.controller.user.dto.AuthResponse;
import com.meoguri.linkocean.controller.user.dto.RefreshAccessTokenRequest;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.test.restdocs.support.RestDocsTestSupport;

class AuthRestDocsTest extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(AuthController.class);

	@Test
	void 사용자_인증_api() throws Exception {
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
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("oAuthType").description("소셜 로그인 타입")
					),
					requestFields(
						fieldWithPath("code").description("인증 코드"),
						fieldWithPath("redirectUri").description("REDIRECT URI")
					),
					responseFields(
						fieldWithPath("accessToken").description("리소스 접근 전용 토큰"),
						fieldWithPath("refreshToken").description("access token 재발급 용도의 토큰"),
						fieldWithPath("tokenType").description("토큰 타입")
					)
				)
			);
	}

	@Test
	void access_token_재발급_api() throws Exception {
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
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("refreshToken").description("재발급 토큰"),
						fieldWithPath("tokenType").description("토큰 타입")
					),
					responseFields(
						fieldWithPath("accessToken").description("리소스 접근 전용 토큰"),
						fieldWithPath("refreshToken").description("access token 재발급 용도의 토큰"),
						fieldWithPath("tokenType").description("토큰 타입")
					)
				)
			);
	}
}
