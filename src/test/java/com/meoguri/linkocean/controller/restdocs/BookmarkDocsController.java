package com.meoguri.linkocean.controller.restdocs;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.meoguri.linkocean.controller.bookmark.BookmarkController;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.bookmark.dto.UpdateBookmarkRequest;
import com.meoguri.linkocean.controller.support.RestDocsTestSupport;

public class BookmarkDocsController extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(BookmarkController.class);

	private long profileId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		profileId = 프로필_등록("hani", List.of("정치", "인문", "사회"));
	}

	@Test
	void 북마크_등록_api() throws Exception {

		final String title = "title1";
		final String memo = "memo";
		final String category = "인문";
		final String openType = "private";

		final RegisterBookmarkRequest registerBookmarkRequest =
			new RegisterBookmarkRequest(링크_메타데이터_얻기("http://www.naver.com"), title, memo, category, openType, null);

		//when
		mockMvc.perform(post(basePath)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(registerBookmarkRequest)))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestFields(
						fieldWithPath("url").description("북마크 url"),
						fieldWithPath("title").optional().description("북마크 제목"),
						fieldWithPath("memo").optional().description("북마크 메모"),
						fieldWithPath("category").optional().description("북마크 카테고리"),
						fieldWithPath("tags").optional().description("북마크 태그 목록"),
						fieldWithPath("openType").description("북마크 공개 여부")
					),
					responseFields(
						fieldWithPath("id").description("북마크 ID")
					)
				)
			);
	}

	@Test
	void 북마크_수정_Api_성공() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		final String updateTitle = "updateTitle";
		final String updateMemo = "updatedMemo";
		final String updateCategory = "IT";
		final String updateOpenType = "all";
		final List<String> updateTags = List.of("Spring", "React", "LinkOcean");

		final UpdateBookmarkRequest updateBookmarkRequest = new UpdateBookmarkRequest(updateTitle, updateMemo,
			updateCategory, updateOpenType, updateTags);
		//when
		mockMvc.perform(put(basePath + "/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(updateBookmarkRequest)))

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
					),
					requestFields(
						fieldWithPath("title").optional().description("북마크 제목"),
						fieldWithPath("memo").optional().description("북마크 메모"),
						fieldWithPath("category").optional().description("북마크 카테고리"),
						fieldWithPath("tags").optional().description("북마크 태그 목록"),
						fieldWithPath("openType").description("북마크 공개 여부")
					)
				)
			);
	}

	@Test
	void 북마크_삭제_api() throws Exception {

		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		// 북마크 삭제
		//when
		mockMvc.perform(RestDocumentationRequestBuilders.delete(basePath + "/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

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

	@Nested
	class 내_북마크_목록_조회_테스트 {

		private long bookmarkId1;
		private long bookmarkId2;

		@BeforeEach
		void setUp() throws Exception {
			bookmarkId1 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
			bookmarkId2 = 북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"), "partial");
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_필터링_조건_없이_조회() throws Exception {
			//when
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks", hasSize(2)),
					jsonPath("$.bookmarks[0].id").value(bookmarkId2),        // 입력 시간 역순으로 조회
					jsonPath("$.bookmarks[1].id").value(bookmarkId1),
					jsonPath("$.bookmarks[1].title").value("title1"),
					jsonPath("$.bookmarks[1].url").value("https://www.naver.com"),
					jsonPath("$.bookmarks[1].openType").value("all"),
					jsonPath("$.bookmarks[1].updatedAt").exists(),
					jsonPath("$.bookmarks[1].imageUrl").exists(),
					jsonPath("$.bookmarks[1].likeCount").value(0),
					jsonPath("$.bookmarks[1].isFavorite").value(false),
					jsonPath("$.bookmarks[1].isWriter").value(true),
					jsonPath("$.bookmarks[1].tags", hasItem("공부"))
				).andDo(print());
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_카테고리_필터링() throws Exception {
			//when
			mockMvc.perform(get(basePath + "/me")
					.param("category", "IT")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].category").value("IT")
				)
				.andDo(print());
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_즐겨찾기_필터링() throws Exception {
			//given
			북마크_즐겨찾기(bookmarkId1);

			//when
			mockMvc.perform(get(basePath + "/me")
					.param("favorite", "true")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].id").value(bookmarkId1),
					jsonPath("$.bookmarks[0].isFavorite").value(true)
				).andDo(print());
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_태그_필터링() throws Exception {
			//when
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.param("tags", "공부,travel")
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2)
				).andDo(print());
		}
	}

	@Test
	void url_중복확인_url() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title1", "IT", List.of("공부"), "all");
		final String expectedLocationHeader = "api/v1/bookmarks/" + bookmarkId;

		//when
		mockMvc.perform(get(basePath + "?url=https://www.google.com")
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpect(header().string("Location", expectedLocationHeader))
			.andExpect(jsonPath("$.isDuplicateUrl").value(true))
			.andDo(print());
	}

	@Nested
	class 다른_사람_북마크_목록_조회_api {

		private long bookmarkId1;
		private long bookmarkId2;
		private long bookmarkId4;

		private long otherProfileId;

		@BeforeEach
		void setUp() throws Exception {

			유저_등록_로그인("crush@gmail.com", "GOOGLE");
			프로필_등록("crush", List.of("IT"));

			유저_등록_로그인("otherUser@gmail.com", "GOOGLE");
			otherProfileId = 프로필_등록("user1", List.of("IT"));

			bookmarkId1 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
			bookmarkId2 = 북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"), "partial");
			북마크_등록(링크_메타데이터_얻기("https://programmers.co.kr"), "title3", "기술", List.of("공부", "코테"), "private");
			bookmarkId4 = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title4", "자기계발", List.of("머구리"), "all");

			로그인("crush@gmail.com", "GOOGLE");
		}

		@Test
		void 팔로우_유저_북마크_목록_조회() throws Exception {
			//given
			팔로우(otherProfileId);

			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(3),
					jsonPath("$.bookmarks[0].id").value(bookmarkId4),
					jsonPath("$.bookmarks[0].openType").value("all"),
					jsonPath("$.bookmarks[1].id").value(bookmarkId2),
					jsonPath("$.bookmarks[1].openType").value("partial"),
					jsonPath("$.bookmarks[2].id").value(bookmarkId1),
					jsonPath("$.bookmarks[2].openType").value("all"))
				.andDo(print());
		}

	}

	@Nested
	class 피드_북마크_조회 {

		private long bookmarkId4;
		private long bookmarkId5;
		private long bookmarkId6;

		private long bookmarkId7;
		private long bookmarkId8;

		private long bookmarkId10;

		@BeforeEach
		void setUp() throws Exception {
			유저_등록_로그인("user3@gmail.com", "GOOGLE");
			프로필_등록("user3", List.of("IT"));

			북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
			북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "partial");
			bookmarkId10 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

			유저_등록_로그인("user2@gmail.com", "GOOGLE");
			final long profileId2 = 프로필_등록("user2", List.of("IT"));

			북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
			bookmarkId8 = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "partial");
			bookmarkId7 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

			유저_등록_로그인("user1@gmail.com", "GOOGLE");
			프로필_등록("user1", List.of("IT"));

			bookmarkId6 = 북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
			bookmarkId5 = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "partial");
			bookmarkId4 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

			팔로우(profileId2);
		}

		@Test
		void 피드_북마크_조회_성공() throws Exception {
			//when
			mockMvc.perform(get(basePath + "/feed")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(6),
					jsonPath("$.bookmarks", hasSize(6)),
					jsonPath("$.bookmarks[0].id").value(bookmarkId4),
					jsonPath("$.bookmarks[1].id").value(bookmarkId5),
					jsonPath("$.bookmarks[2].id").value(bookmarkId6),
					jsonPath("$.bookmarks[3].id").value(bookmarkId7),
					jsonPath("$.bookmarks[4].id").value(bookmarkId8),
					jsonPath("$.bookmarks[5].id").value(bookmarkId10)
				).andDo(print());
		}

		@Test
		void 피드_북마크_조회_팔로우_여부로_성공() throws Exception {
			//when
			mockMvc.perform(get(basePath + "/feed")
					.param("follow", "true")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks", hasSize(2)),
					jsonPath("$.bookmarks[0].id").value(bookmarkId7),
					jsonPath("$.bookmarks[1].id").value(bookmarkId8)
				).andDo(print());
		}

		@Test
		void 피드_북마크_조회_즐겨찾기_후_조회_성공() throws Exception {
			로그인("user2@gmail.com", "GOOGLE");
			북마크_즐겨찾기(bookmarkId10);

			//when
			mockMvc.perform(get(basePath + "/feed")
					.param("favorite", "true")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks", hasSize(1)),
					jsonPath("$.bookmarks[0].isWriter").value(false)
				).andDo(print());
		}
	}
}
