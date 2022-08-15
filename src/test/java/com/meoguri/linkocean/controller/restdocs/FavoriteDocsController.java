package com.meoguri.linkocean.controller.restdocs;

import static org.apache.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.meoguri.linkocean.controller.bookmark.ReactionController;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

public class FavoriteDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(ReactionController.class);

	private long bookmarkId;

	@Test
	void 즐겨찾기_추가_Api() throws Exception {
		유저_등록_로그인("haha@gmail.com", "NAVER");
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");

		//when
		mockMvc.perform(RestDocumentationRequestBuilders
				.post(basePath + "/{bookmarkId}/favorite", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("bookmarkId").description("북마크 ID")
					)
				)
			);
	}

	@Test
	void 즐겨찾기_취소_Api() throws Exception {

		//given
		유저_등록_로그인("haha@gmail.com", "NAVER");
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");

		/* 북마크 즐겨찾기 추가 */
		mockMvc.perform(post(basePath + "/{bookmarkId}/favorite", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//when
		mockMvc.perform(RestDocumentationRequestBuilders.post(basePath + "/{bookmarkId}/unfavorite", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("")
					),
					pathParameters(
						parameterWithName("bookmarkId").description("북마크 ID")
					)
				)
			);
	}
}
