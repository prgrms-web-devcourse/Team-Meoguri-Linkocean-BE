package com.meoguri.linkocean.controller.category;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.bookmark.CategoryController;

class CategoryControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(CategoryController.class);

	@Test
	void 카테고리_전체_조회_Api_성공() throws Exception {
		//when
		mockMvc.perform(get(basePath)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpectAll(
				// jsonPath("$.count").value(12),
				jsonPath("$.categories").isArray(),
				jsonPath("$.categories", hasSize(12)),
				jsonPath("$.categories",
					hasItems("자기계발", "인문", "정치", "사회", "예술", "과학", "기술", "IT", "가정", "건강", "여행", "요리"))
			).andDo(print());
	}
}
