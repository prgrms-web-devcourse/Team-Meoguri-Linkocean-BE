package com.meoguri.linkocean.controller.restdocs;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
	void 북마크_수정_api() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		final String updateTitle = "updateTitle";
		final String updateMemo = "updatedMemo";
		final String updateCategory = "IT";
		final String updateOpenType = "all";
		final List<String> updateTags = List.of("Spring", "React", "LinkOcean");

		final UpdateBookmarkRequest updateBookmarkRequest = new UpdateBookmarkRequest(
			updateTitle, updateMemo, updateCategory, updateOpenType, updateTags);

		//when
		mockMvc.perform(RestDocumentationRequestBuilders.put(basePath + "/{bookmarkId}", bookmarkId)
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

	@Test
	void 내_북마크_목록_조회_api() throws Exception {
		long bookmarkId1 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
		long bookmarkId2 = 북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"),
			"partial");

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
				jsonPath("$.bookmarks[1].createdAt").exists(),
				jsonPath("$.bookmarks[1].imageUrl").exists(),
				jsonPath("$.bookmarks[1].likeCount").value(0),
				jsonPath("$.bookmarks[1].isFavorite").value(false),
				jsonPath("$.bookmarks[1].isWriter").value(true),
				jsonPath("$.bookmarks[1].tags", hasItem("공부"))
			)

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("북마크 개수(size)"),
						parameterWithName("order").optional().description("북마크 정렬 기준"),
						parameterWithName("tags").optional().description("필터링 태그 목록"),
						parameterWithName("category").optional().description("카테고리"),
						parameterWithName("searchTitle").optional().description(""),
						parameterWithName("favorite").optional().description("필터링 태그 목록")
					),
					responseFields(
						fieldWithPath("totalCount").description("북마크 총 개수"),
						fieldWithPath("bookmarks[]").optional().description("북마크 목록"),
						fieldWithPath("bookmarks[].id").description("북마크 ID"),
						fieldWithPath("bookmarks[].title").description("북마크 제목"),
						fieldWithPath("bookmarks[].url").description("북마크 url"),
						fieldWithPath("bookmarks[].openType").description("북마크 공개 범위"),
						fieldWithPath("bookmarks[].category").description("북마크 카테고리"),
						fieldWithPath("bookmarks[].createdAt").description("북마크 생성 일자"),
						fieldWithPath("bookmarks[].imageUrl").description("북마크 이미지 url"),
						fieldWithPath("bookmarks[].likeCount").description("북마크 좋아요 수"),
						fieldWithPath("bookmarks[].isFavorite").description("북마크 즐겨찾기 여부"),
						fieldWithPath("bookmarks[].isWriter").description("북마크 작성자 여부"),
						fieldWithPath("bookmarks[].tags[]").optional().description("북마크 태그 목록")
					)
				)
			);
	}

	@Test
	void url_중복확인_api() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title1", "IT", List.of("공부"), "all");
		final String expectedLocationHeader = "api/v1/bookmarks/" + bookmarkId;

		//when
		mockMvc.perform(RestDocumentationRequestBuilders.get(basePath + "?url=https://www.google.com")
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpect(header().string("Location", expectedLocationHeader))
			.andExpect(jsonPath("$.isDuplicateUrl").value(true))

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("url").description("북마크 url")
					),
					responseHeaders(
						headerWithName("Location").optional().description("중복된 북마크 location")
					),
					responseFields(
						fieldWithPath("isDuplicateUrl").optional().description("중복 여부")
					)
				)
			);
	}

	@Test
	void 다른_사람_북마크_목록_조회_api() throws Exception {
		//given
		유저_등록_로그인("crush@gmail.com", "GOOGLE");
		프로필_등록("crush", List.of("IT"));

		유저_등록_로그인("otherUser@gmail.com", "GOOGLE");
		long otherProfileId = 프로필_등록("user1", List.of("IT"));

		long bookmarkId1 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
		long bookmarkId2 = 북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"),
			"partial");
		북마크_등록(링크_메타데이터_얻기("https://programmers.co.kr"), "title3", "기술", List.of("공부", "코테"), "private");
		long bookmarkId4 = 북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title4", "자기계발", List.of("머구리"), "all");

		로그인("crush@gmail.com", "GOOGLE");

		팔로우(otherProfileId);

		//when then
		mockMvc.perform(RestDocumentationRequestBuilders.get(basePath + "/others/{profileId}", otherProfileId)
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

			//docs
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("profileId").description("다른 사용자 프로필 ID")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("북마크 개수(size)"),
						parameterWithName("order").optional().description("북마크 정렬 기준"),
						parameterWithName("tags").optional().description("필터링 태그 목록"),
						parameterWithName("category").optional().description("카테고리"),
						parameterWithName("searchTitle").optional().description("제목 검색"),
						parameterWithName("favorite").optional().description("필터링 태그 목록")
					),
					responseFields(
						fieldWithPath("totalCount").description("북마크 총 개수"),
						fieldWithPath("bookmarks[]").optional().description("북마크 목록"),
						fieldWithPath("bookmarks[].id").description("북마크 ID"),
						fieldWithPath("bookmarks[].title").description("북마크 제목"),
						fieldWithPath("bookmarks[].url").description("북마크 url"),
						fieldWithPath("bookmarks[].openType").description("북마크 공개 범위"),
						fieldWithPath("bookmarks[].category").description("북마크 카테고리"),
						fieldWithPath("bookmarks[].createdAt").description("북마크 생성 일자"),
						fieldWithPath("bookmarks[].imageUrl").description("북마크 이미지 url"),
						fieldWithPath("bookmarks[].likeCount").description("북마크 좋아요 수"),
						fieldWithPath("bookmarks[].isFavorite").description("북마크 즐겨찾기 여부"),
						fieldWithPath("bookmarks[].isWriter").description("북마크 작성자 여부"),
						fieldWithPath("bookmarks[].tags[]").optional().description("북마크 태그 목록")
					)
				)
			);
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
		void 피드_북마크_조회_api() throws Exception {
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
				)

				//docs
				.andDo(
					restDocs.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("인증 토큰")
						),
						requestParameters(
							parameterWithName("page").optional().description("현재 페이지(page)"),
							parameterWithName("size").optional().description("북마크 개수(size)"),
							parameterWithName("order").optional().description("북마크 정렬 기준"),
							parameterWithName("category").optional().description("카테고리"),
							parameterWithName("searchTitle").optional().description("제목 검색"),
							parameterWithName("follow").optional().description("팔로우 여부")
						),
						responseFields(
							fieldWithPath("totalCount").description("북마크 총 개수"),
							fieldWithPath("bookmarks[]").optional().description("북마크 목록"),
							fieldWithPath("bookmarks[].id").description("북마크 ID"),
							fieldWithPath("bookmarks[].title").description("북마크 제목"),
							fieldWithPath("bookmarks[].url").description("북마크 url"),
							fieldWithPath("bookmarks[].openType").description("북마크 공개 범위"),
							fieldWithPath("bookmarks[].category").description("북마크 카테고리"),
							fieldWithPath("bookmarks[].createdAt").description("북마크 생성 일자"),
							fieldWithPath("bookmarks[].imageUrl").description("북마크 이미지 url"),
							fieldWithPath("bookmarks[].likeCount").description("북마크 좋아요 수"),
							fieldWithPath("bookmarks[].isFavorite").description("북마크 즐겨찾기 여부"),
							fieldWithPath("bookmarks[].isWriter").description("북마크 작성자 여부"),
							fieldWithPath("bookmarks[].tags[]").optional().description("북마크 태그 목록"),
							fieldWithPath("bookmarks[].profile").description("작성자"),
							fieldWithPath("bookmarks[].profile.profileId").description("작성자 ID"),
							fieldWithPath("bookmarks[].profile.imageUrl").description("작성자 이미지 url"),
							fieldWithPath("bookmarks[].profile.username").description("작성자 이름"),
							fieldWithPath("bookmarks[].profile.isFollow").description("작성자 팔로우 여부")
						)
					)
				);
		}

	}
}
