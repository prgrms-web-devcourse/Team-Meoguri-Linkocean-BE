package com.meoguri.linkocean.test.restdocs;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.user.AuthController;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.service.OAuthClient;
import com.meoguri.linkocean.test.restdocs.support.RestDocsTestSupport;

class AuthRestDocsTest extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(AuthController.class);

	@MockBean
	private OAuthClient oAuthClient;

	@Test
	void 사용자_인증_api() throws Exception {
		//given
		final String oAuthType = "google";
		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@google.com"));

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{oAuthType}", oAuthType)
			.param("code", "code")
			.accept(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("oAuthType").description("소셜 로그인 타입")
					),
					requestParameters(
						parameterWithName("code").description("서드 파티 인증 서버에게 받은 인증 코드")
					),
					responseFields(
						fieldWithPath("token").description("jwt 토큰")
					)
				)
			);
	}
}
