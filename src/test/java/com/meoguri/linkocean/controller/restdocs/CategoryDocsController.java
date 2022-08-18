package com.meoguri.linkocean.controller.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.bookmark.CategoryController;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

class CategoryDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(CategoryController.class);

	@Test
	void 카테고리_전체_조회_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hello@gmail.com", GOOGLE);

		//when
		mockMvc.perform(get(basePath)
				.contentType(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION, token))

			//then
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
