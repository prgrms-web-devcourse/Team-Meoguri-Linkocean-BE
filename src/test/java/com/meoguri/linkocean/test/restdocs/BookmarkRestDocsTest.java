package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.model.OAuthType.*;
import static java.util.Collections.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.bookmark.BookmarkController;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.bookmark.dto.UpdateBookmarkRequest;
import com.meoguri.linkocean.test.restdocs.support.RestDocsTestSupport;

class BookmarkRestDocsTest extends RestDocsTestSupport {

	private final String basePath = getBaseUrl(BookmarkController.class);

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("정치", "인문", "사회"));
	}

	@Test
	void 북마크_등록_api() throws Exception {
		//given
		final String title = "title1";
		final String memo = "memo";
		final String category = "인문";
		final String openType = "private";

		final RegisterBookmarkRequest registerBookmarkRequest =
			new RegisterBookmarkRequest(링크_메타데이터_얻기("http://www.naver.com"), title, memo, category, openType, null);

		//when
		final ResultActions perform = mockMvc.perform(post(basePath)
			.header(AUTHORIZATION, token)
			.contentType(APPLICATION_JSON)
			.content(createJson(registerBookmarkRequest)));

		//then
		perform
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
						fieldWithPath("openType").description("북마크 공개 여부")),
					responseFields(
						fieldWithPath("id").description("북마크 ID")
					)
				)
			);
	}

	@Test
	void 내_북마크_목록_조회_api() throws Exception {
		북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
		북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"), "private");

		//when
		final ResultActions perform = mockMvc.perform(get(basePath + "/me")
			.header(AUTHORIZATION, token)
			.accept(APPLICATION_JSON));

		//then
		perform
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
						parameterWithName("searchTitle").optional().description("검색 제목"),
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
	void 다른_사람_북마크_목록_조회_api() throws Exception {
		//given
		유저_등록_로그인("crush@gmail.com", GOOGLE);
		프로필_등록("crush", List.of("IT"));

		유저_등록_로그인("otherUser@gmail.com", GOOGLE);
		final long otherProfileId = 프로필_등록("user1", List.of("IT"));

		북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "title1", "IT", List.of("공부"), "all");
		북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "title2", "여행", List.of("travel"), "all");
		북마크_등록(링크_메타데이터_얻기("https://programmers.co.kr"), "title3", "기술", List.of("공부", "코테"), "private");
		북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title4", "자기계발", List.of("머구리"), "all");

		로그인("crush@gmail.com", GOOGLE);

		팔로우(otherProfileId);

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.get(basePath + "/others/{profileId}", otherProfileId)
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(headerWithName(AUTHORIZATION).description("인증 토큰")
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
						fieldWithPath("bookmarks[].tags[]").optional().description("북마크 태그 목록"))
				)
			);
	}

	@Test
	void 피드_북마크_조회_api() throws Exception {
		//given
		유저_등록_로그인("user3@gmail.com", GOOGLE);
		프로필_등록("user3", List.of("IT"));

		북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
		북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "all");
		북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

		유저_등록_로그인("user2@gmail.com", GOOGLE);
		final long profileId2 = 프로필_등록("user2", List.of("IT"));

		북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
		북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "all");
		북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

		유저_등록_로그인("user1@gmail.com", GOOGLE);
		프로필_등록("user1", List.of("IT"));

		북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");
		북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "all");
		북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "all");

		팔로우(profileId2);

		//when
		final ResultActions perform = mockMvc.perform(get(basePath + "/feed")
			.header(AUTHORIZATION, token)
			.accept(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(headerWithName(AUTHORIZATION).description("인증 토큰")
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

	@Test
	void 북마크_상세_조회_api() throws Exception {
		//given
		유저_등록_로그인("user1@gmail.com", GOOGLE);
		프로필_등록("user1", List.of("IT"));

		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.github.com"), "private");

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.get(basePath + "/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("bookmarkId").description("북마크 ID")
					),
					responseFields(
						fieldWithPath("bookmarkId").description("북마크 아이디"),
						fieldWithPath("title").description("북마크 제목"), fieldWithPath("url").description("북마크 url"),
						fieldWithPath("imageUrl").description("북마크 이미지 Url"),
						fieldWithPath("category").description("북마크 카테고리"),
						fieldWithPath("memo").description("북마크 메모"), fieldWithPath("openType").description("북마크 공개 범위"),
						fieldWithPath("isFavorite").description("즐겨찾기 여부"),
						fieldWithPath("createdAt").description("북마크 생성일"),
						fieldWithPath("tags[]").optional().description("북마크 태그 목록"),
						fieldWithPath("reactionCount").description("리액션 현황"),
						fieldWithPath("reactionCount.LIKE").description("좋아요 개수"),
						fieldWithPath("reactionCount.HATE").description("싫어요 개수"),
						fieldWithPath("reaction").description("리액션 여부"),
						fieldWithPath("reaction.LIKE").description("좋아요 여부"),
						fieldWithPath("reaction.HATE").description("싫어요 여부"),
						fieldWithPath("profile").description("작성자 정보"),
						fieldWithPath("profile.profileId").description("작성자 아이디"),
						fieldWithPath("profile.username").description("작성자 유저 이름"),
						fieldWithPath("profile.imageUrl").description("작성자 프로필 이미지 url"),
						fieldWithPath("profile.isFollow").description("작성자 팔로우 여부")
					)
				)
			);
	}

	@Test
	void 북마크_업데이트_api() throws Exception {
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
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.put(basePath + "/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(updateBookmarkRequest)));

		//then
		perform
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
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.delete(basePath + "/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON));

		//then
		perform
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
	void 북마크_공유_알림_api() throws Exception {
		//given
		유저_등록_로그인("sender@gmail.com", GOOGLE);
		final long senderProfileId = 프로필_등록("sender", List.of("IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");

		유저_등록_로그인("target@gmail.com", GOOGLE);
		final long targetProfileId = 프로필_등록("target", List.of("IT"));
		북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, emptyList(), "all");
		팔로우(senderProfileId);

		로그인("sender@gmail.com", GOOGLE);
		final Map<String, Long> request = Map.of("targetId", targetProfileId);

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.post(basePath + "/{bookmarkId}/share", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(request)));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("bookmarkId").description("북마크 아이디")
					),
					requestFields(
						fieldWithPath("targetId").description("공유 보낼 사용자 프로필 아이디"))
				)
			);
	}

	@Test
	void 중복_url_확인_api() throws Exception {
		//given
		북마크_등록(링크_메타데이터_얻기("https://www.google.com"), "title1", "IT", List.of("공부"), "all");

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.get(basePath + "?url=https://www.google.com")
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("url").description("북마크 url")
					),
					responseHeaders(
						headerWithName(LOCATION).optional().description("중복된 북마크 location")
					),
					responseFields(
						fieldWithPath("isDuplicateUrl").description("중복 여부")
					)
				)
			);
	}
}
