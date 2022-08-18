package com.meoguri.linkocean.controller.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.notification.NotificationController;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

public class NotificationDocsController extends RestDocsTestSupport {

	private final String baseUrl = getBaseUrl(NotificationController.class);

	private long senderProfileId;
	private long shareBookmarkId;
	private long targetProfileId;
	private long unsharableBookmarkId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("sender@gmail.com", GOOGLE);
		senderProfileId = 프로필_등록("sender", List.of("IT"));
		shareBookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");

		유저_등록_로그인("target@gmail.com", GOOGLE);
		targetProfileId = 프로필_등록("target", List.of("IT"));
		unsharableBookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");
		팔로우(senderProfileId);
	}

	@Test
	void 공유_알림_생성하고_조회_성공() throws Exception {
		//given
		로그인("sender@gmail.com", GOOGLE);
		final Map<String, Long> request = Map.of(
			"targetId", targetProfileId
		);

		//when
		//공유 알림 하나 생성
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}" + "/share", shareBookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(request)))
			//then
			.andExpect(status().isOk())
			.andDo(print());

		//when
		//공유 알림 조회
		로그인("target@gmail.com", GOOGLE);

		mockMvc.perform(get(baseUrl)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpectAll(

				jsonPath("$.notifications", hasSize(1)),
				jsonPath("$.notifications[0].info").exists(),

				jsonPath("$.notifications[0].info.sender").exists(),
				jsonPath("$.notifications[0].info.sender.id").value(senderProfileId),
				jsonPath("$.notifications[0].info.sender.username").value("sender"),

				jsonPath("$.notifications[0].info.bookmark").exists(),
				jsonPath("$.notifications[0].info.bookmark.id").value(shareBookmarkId),
				jsonPath("$.notifications[0].info.bookmark.title").value("title"),
				jsonPath("$.notifications[0].info.bookmark.link").value("http://www.naver.com")
			)

			//docs
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
