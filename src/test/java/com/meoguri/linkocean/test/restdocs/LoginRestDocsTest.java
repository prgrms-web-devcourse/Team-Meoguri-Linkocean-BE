package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.model.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.user.LoginController;
import com.meoguri.linkocean.controller.user.dto.LoginRequest;
import com.meoguri.linkocean.test.restdocs.support.RestDocsTestSupport;

class LoginRestDocsTest extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(LoginController.class);

	@Test
	void 로그인_api() throws Exception {
		//given
		final String email = "jk05018@naver.com";
		final String oauthType = "NAVER";

		final LoginRequest loginRequest = new LoginRequest(email, oauthType);

		//when
		final ResultActions perform = mockMvc.perform(post(basePath)
			.contentType(MediaType.APPLICATION_JSON)
			.content(createJson(loginRequest)));

		//then
		perform
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("oauthType").description("소셜 타입[GOOGLE, GITHUB, NAVER]")
					),
					responseFields(
						fieldWithPath("token").description("jwt 토큰")
					)
				)
			);
	}

	@Test
	void 사용자_프로필_유무_확인_api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		//when
		final ResultActions perform = mockMvc.perform(get(basePath + "/success")
			.header(AUTHORIZATION, token)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					responseFields(
						fieldWithPath("hasProfile").description("프로필 유무")
					)
				)
			);
	}
}
