package com.meoguri.linkocean.controller.restdocs;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.bookmark.CategoryController;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

public class CategoryDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(CategoryController.class);

	@Test
	void 카테고리_전체_조회_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hello@gmail.com", "GOOGLE");

		//when
		mockMvc.perform(get(basePath)
				.contentType(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION, token))
			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.categories").isArray(),
				jsonPath("$.categories", hasSize(12)),
				jsonPath("$.categories",
					hasItems("자기계발", "인문", "정치", "사회", "예술", "과학", "기술", "IT", "가정", "건강", "여행", "요리"))
			)

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					responseFields(
						fieldWithPath("categories[]").description("카테고리 목록")
					)
				)
			);
	}

}
