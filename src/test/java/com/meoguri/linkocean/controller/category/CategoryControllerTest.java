package com.meoguri.linkocean.controller.category;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark.Category;

class CategoryControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(CategoryController.class);

	@WithMockUser(roles = "USER")
	@Test
	void 카테고리_전체_조회_Api_성공() throws Exception {
		//when
		mockMvc.perform(get(basePath)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.count").value(12),
				jsonPath("$.categories").isArray(),
				jsonPath("$.categories", hasSize(12)),
				jsonPath("$.categories[0]").value(Category.getKoreanNames().get(0)),
				jsonPath("$.categories[1]").value(Category.getKoreanNames().get(1)),
				jsonPath("$.categories[2]").value(Category.getKoreanNames().get(2)),
				jsonPath("$.categories[3]").value(Category.getKoreanNames().get(3)),
				jsonPath("$.categories[4]").value(Category.getKoreanNames().get(4)),
				jsonPath("$.categories[5]").value(Category.getKoreanNames().get(5)),
				jsonPath("$.categories[6]").value(Category.getKoreanNames().get(6)),
				jsonPath("$.categories[7]").value(Category.getKoreanNames().get(7)),
				jsonPath("$.categories[8]").value(Category.getKoreanNames().get(8)),
				jsonPath("$.categories[9]").value(Category.getKoreanNames().get(9)),
				jsonPath("$.categories[10]").value(Category.getKoreanNames().get(10)),
				jsonPath("$.categories[11]").value(Category.getKoreanNames().get(11))
			).andDo(print());
	}
}
