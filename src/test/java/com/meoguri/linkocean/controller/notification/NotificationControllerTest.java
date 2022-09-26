package com.meoguri.linkocean.controller.notification;

import static com.meoguri.linkocean.domain.user.model.OAuthType.*;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class NotificationControllerTest extends BaseControllerTest {

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
		unsharableBookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "private");
		팔로우(senderProfileId);
	}

	@Test
	void 공유_알림_생성_Api_성공() throws Exception {
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
		mockMvc.perform(get("/api/v1/notifications")
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
			.andDo(print());
	}

	@Test
	void 공유_알림_생성_Api_실패_대상이_나를_팔로우_중이_아님() throws Exception {
		//given
		로그인("target@gmail.com", GOOGLE);
		final Map<String, Object> request = Map.of(
			"targetId", senderProfileId
		);

		//when
		final ResultActions perform = mockMvc.perform(
			post("/api/v1/bookmarks/{bookmarkId}" + "/share", unsharableBookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(request)));

		//then
		perform
			.andExpect(status().isBadRequest());
	}

	@Test
	void 공유_알림_생성_Api_실패_북마크_공개범위_private() throws Exception {
		//given
		로그인("sender@gmail.com", GOOGLE);
		final Map<String, Object> request = Map.of(
			"targetId", targetProfileId
		);

		//when
		final ResultActions perform = mockMvc.perform(
			post("/api/v1/bookmarks/{bookmarkId}" + "/share", unsharableBookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(request)));

		//then
		perform
			.andExpect(status().isBadRequest());
	}
}
