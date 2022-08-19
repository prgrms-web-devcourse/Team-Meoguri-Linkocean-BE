package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.meoguri.linkocean.controller.bookmark.ReactionController;
import com.meoguri.linkocean.test.support.controller.RestDocsTestSupport;

class ReactionDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(ReactionController.class);

	private long bookmarkId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("haha@gmail.com", NAVER);
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "IT", null, "all");
	}

	@Test
	void 리액션_등록_수정_삭제Api() throws Exception {
		//when
		mockMvc.perform(RestDocumentationRequestBuilders
				.post(basePath + "/{bookmarkId}/reactions/{reactionType}", bookmarkId, "like")
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("bookmarkId").description("북마크 ID"),
						parameterWithName("reactionType").description("리액션 타입 ex) like, hate")
					)
				)
			);
	}
}
