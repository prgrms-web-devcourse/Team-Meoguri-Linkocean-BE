package com.meoguri.linkocean.controller.restdocs;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import com.meoguri.linkocean.controller.linkmetadata.LinkMetadataController;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

public class LinkMetadataDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(LinkMetadataController.class);

	@Test
	void 링크메타데이터_제목_조회_Api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		final String link = "http://www.naver.com";
		//when
		mockMvc.perform(post(UriComponentsBuilder.fromUriString(basePath)
				.pathSegment("obtain")
				.queryParam("link", link)
				.build()
				.toUri())
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").exists())

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("link").description("url 링크")
					),
					responseFields(
						fieldWithPath("title").optional().description("링크 메타데이터 제목")
					)
				)
			);

	}
}