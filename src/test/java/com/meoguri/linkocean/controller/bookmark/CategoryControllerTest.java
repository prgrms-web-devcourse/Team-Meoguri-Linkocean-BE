package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class CategoryControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(CategoryController.class);

	@Test
	void 카테고리_전체_조회_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hello@gmail.com", GOOGLE);

		//when
		final ResultActions perform = mockMvc.perform(get(basePath)
			.contentType(MediaType.APPLICATION_JSON)
			.header(AUTHORIZATION, token));

		//then
		perform
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.categories").isArray(),
				jsonPath("$.categories", hasSize(12)),
				jsonPath("$.categories",
					hasItems("자기계발", "인문", "정치", "사회", "예술", "과학", "기술", "IT", "가정", "건강", "여행", "요리"))
			).andDo(print());
	}
}
