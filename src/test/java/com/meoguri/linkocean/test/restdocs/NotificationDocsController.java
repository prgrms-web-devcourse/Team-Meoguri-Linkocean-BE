package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.Collections.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.notification.NotificationController;
import com.meoguri.linkocean.test.support.controller.RestDocsTestSupport;

class NotificationDocsController extends RestDocsTestSupport {

	private final String baseUrl = getBaseUrl(NotificationController.class);

	private long senderProfileId;
	private long bookmarkId;
	private long targetProfileId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("sender@gmail.com", GOOGLE);
		senderProfileId = 프로필_등록("sender", List.of("IT"));
		bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");

		유저_등록_로그인("target@gmail.com", GOOGLE);
		targetProfileId = 프로필_등록("target", List.of("IT"));
		북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");
		팔로우(senderProfileId);
	}

	@Test
	void 공유_알림_생성하고_조회_성공() throws Exception {
		//given
		로그인("sender@gmail.com", GOOGLE);
		북마크_공유(bookmarkId, targetProfileId);

		로그인("target@gmail.com", GOOGLE);

		//when
		mockMvc.perform(get(baseUrl)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("프로필 개수(size)")
					),
					responseFields(
						fieldWithPath("hasNext").optional().description("프로필 리스트"),
						fieldWithPath("notifications[]").optional().description("프로필 리스트"),
						fieldWithPath("notifications[].type").description("프로필 ID"),
						fieldWithPath("notifications[].info").description("알림 정보"),
						fieldWithPath("notifications[].info.bookmark").description("알림 정보 - 북마크"),
						fieldWithPath("notifications[].info.bookmark.id").description("알림 정보 - 북마크 ID"),
						fieldWithPath("notifications[].info.bookmark.title").description("알림 정보 - 북마크 제목"),
						fieldWithPath("notifications[].info.bookmark.link").description("알림 정보 - 북마크 링크"),
						fieldWithPath("notifications[].info.sender").description("알림 정보 - 송신자"),
						fieldWithPath("notifications[].info.sender.id").description("알림 정보 - 송신자 프로필 ID"),
						fieldWithPath("notifications[].info.sender.username").description("알림 정보 - 송신자 유저 이름")
					)
				)
			);
	}
}
