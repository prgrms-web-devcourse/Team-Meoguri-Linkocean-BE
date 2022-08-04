package com.meoguri.linkocean.controller.user;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.BaseControllerTest;

@Disabled
class LoginControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(LoginController.class);

	@Test
	void 로그인_성공후_Profile_소지여부조회_Api_hasProfile_true() throws Exception {
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
			.andDo(print());

	}

	@Test
	void 로그인_성공후_Profile_소지여부조회_Api_hasProfile_false() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");

		//when
		mockMvc.perform(get(basePath + "/success")
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasProfile").value(false))
			.andDo(print());

	}

}
