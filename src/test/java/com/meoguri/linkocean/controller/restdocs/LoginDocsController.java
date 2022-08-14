package com.meoguri.linkocean.controller.restdocs;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.support.RestDocsTestSupport;
import com.meoguri.linkocean.controller.user.LoginController;
import com.meoguri.linkocean.controller.user.dto.LoginRequest;

public class LoginDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(LoginController.class);

	@Test
	void 로그인_api() throws Exception {
		//given
		final String email = "jk05018@naver.com";
		final String oauthType = "NAVER";

		final LoginRequest loginRequest = new LoginRequest(email, oauthType);

		//when
		mockMvc.perform(post(basePath)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson(loginRequest)))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists())

			//docs
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("oauthType").description("소셜 타입[GOOGLE, NAVER, KAKAO]")
					),
					responseFields(
						fieldWithPath("token").description("jwt 토큰")
					)
				)
			);

	}

	@Test
	void 로그인_성공_api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		//when
		mockMvc.perform(get(basePath + "/success")
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasProfile").value(true))
			.andDo(print())

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					responseFields(
						fieldWithPath("hasProfile").description("이메일")
					)
				)
			);

	}
}
