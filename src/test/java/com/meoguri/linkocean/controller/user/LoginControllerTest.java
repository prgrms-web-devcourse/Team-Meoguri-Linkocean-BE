package com.meoguri.linkocean.controller.user;

import static com.meoguri.linkocean.domain.user.domain.model.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.user.dto.LoginRequest;
import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class LoginControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(LoginController.class);

	@Test
	void 로그인_Api_성공() throws Exception {
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
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void 프로필_존재_유무_조회_Api_성공_프로필_있음() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		//when
		final ResultActions perform = mockMvc.perform(get(basePath + "/success")
			.header(AUTHORIZATION, token)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasProfile").value(true))
			.andDo(print());
	}

	@Test
	void 프로필_존재_유무_조회_Api_성공_프로필_없음() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);

		//when
		final ResultActions perform = mockMvc.perform(get(basePath + "/success")
			.header(AUTHORIZATION, token)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasProfile").value(false))
			.andDo(print());
	}
}
