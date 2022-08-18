package com.meoguri.linkocean.controller.bookmark;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.bookmark.dto.UpdateBookmarkRequest;
import com.meoguri.linkocean.controller.support.BaseControllerTest;

class BookmarkControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(BookmarkController.class);

	private long profileId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		profileId = 프로필_등록("hani", List.of("정치", "인문", "사회"));
	}

	@Test
	void 북마크_등록_Api_성공() throws Exception {

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
			.andDo(print());
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
		mockMvc.perform(put(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(updateBookmarkRequest)))

			//then
			.andExpect(status().isOk())
			.andDo(print());

		// 수정한 북마크 조회
		mockMvc.perform(get(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.bookmarkId").value(bookmarkId),
				jsonPath("$.title").value(updateTitle),
				jsonPath("$.url").value("https://www.naver.com"),
				jsonPath("$.imageUrl").exists(),
				jsonPath("$.category").value(updateCategory),
				jsonPath("$.memo").value(updateMemo),
				jsonPath("$.openType").value(updateOpenType),
				jsonPath("$.isFavorite").value(false),
				jsonPath("$.createdAt").exists(),
				jsonPath("$.tags", hasItems("Spring", "React", "LinkOcean")),
				jsonPath("$.reactionCount.LIKE").value(0),
				jsonPath("$.reactionCount.HATE").value(0),
				jsonPath("$.reaction.LIKE").value(false),
				jsonPath("$.reaction.HATE").value(false),
				jsonPath("$.profile.profileId").value(profileId),
				jsonPath("$.profile.username").value("hani"),
				jsonPath("$.profile.imageUrl").value(nullValue()),
				jsonPath("$.profile.isFollow").value(false)
			).andDo(print());
	}

	@Test
	void 북마크_삭제_Api_성공() throws Exception {

		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		// 북마크 삭제
		//when
		mockMvc.perform(delete(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andDo(print());

		// 삭제한 북마크 조회
		mockMvc.perform(get(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			//then
			.andExpect(status().isBadRequest());
	}

	@Test
	void 제목_메모_카테고리_없는_북마크_상세_조회_Api_성공() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"),
			"title", "IT", List.of("good", "spring"), "all");

		//when
		mockMvc.perform(get(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.bookmarkId").value(bookmarkId),
				jsonPath("$.title").value("title"),
				jsonPath("$.url").value("https://www.naver.com"),
				jsonPath("$.imageUrl").exists(),
				jsonPath("$.category").value("IT"),
				jsonPath("$.memo").value("memo"),
				jsonPath("$.openType").value("all"),
				jsonPath("$.isFavorite").value(false),
				jsonPath("$.createdAt").exists(),
				jsonPath("$.tags", hasItems("good", "spring")),
				jsonPath("$.reactionCount.LIKE").value(0),
				jsonPath("$.reactionCount.HATE").value(0),
				jsonPath("$.reaction.LIKE").value(false),
				jsonPath("$.reaction.HATE").value(false),
				jsonPath("$.profile.profileId").value(profileId),
				jsonPath("$.profile.username").value("hani"),
				jsonPath("$.profile.imageUrl").value(nullValue()),
				jsonPath("$.profile.isFollow").value(false)
			).andDo(print());
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
					jsonPath("$.bookmarks[1].createdAt").exists(),
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
	void Url_중복확인_성공_새로운_url() throws Exception {
		//given <- ?!?!?
		final String locationHeader = "Location";

		//when
		mockMvc.perform(get(basePath + "?url=https://www.google.com")
				.header(AUTHORIZATION, token)
				.accept(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.isDuplicateUrl").value(false)
			).andDo(print());
	}

	@Test
	void Url_중복확인_성공_이미있는_url() throws Exception {
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
	class 다른_유저_북마크_목록_조회_테스트 {

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
		void 모르는_유저_북마크_목록_조회() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks[0].id").value(bookmarkId4),
					jsonPath("$.bookmarks[0].openType").value("all"),
					jsonPath("$.bookmarks[1].id").value(bookmarkId1),
					jsonPath("$.bookmarks[1].openType").value("all"))
				.andDo(print());
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

		@Test
		void 모르는_유저_북마크_목록_조회_즐겨찾기_필터링() throws Exception {
			//given
			로그인("otherUser@gmail.com", "GOOGLE");
			북마크_즐겨찾기(bookmarkId1);

			로그인("crush@gmail.com", "GOOGLE");

			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.param("favorite", "true")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].id").value(bookmarkId1),
					jsonPath("$.bookmarks[0].isFavorite").value(false), //사용자 기준 즐겨찾기 이므로 false가 맞다.
					jsonPath("$.bookmarks[0].openType").value("all"));
		}

		@Test
		void 모르는_유저_북마크_목록_조회_카테고리_필터링() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.param("category", "IT")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].id").value(bookmarkId1),
					jsonPath("$.bookmarks[0].category").value("IT"));
		}

		@Test
		void 모르는_유저_북마크_목록_조회_태그_두개로_필터링() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.param("tags", "머구리", "공부")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks[0].id").value(bookmarkId4),
					jsonPath("$.bookmarks[1].id").value(bookmarkId1)
					// 다른 사용자의 private bookmark 는 조회할 수 없음
				);
		}

		@Test
		void 모르는_유저_북마크_목록_조회_제목으로_필터링() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.param("searchTitle", "1")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].id").value(bookmarkId1),
					jsonPath("$.bookmarks[0].title").value("title1"));
		}

		@Test
		void 모르는_유저_북마크_목록_조회_좋아요_순으로_정렬() throws Exception {
			//given
			북마크_좋아요(bookmarkId1);

			//when then
			mockMvc.perform(get(basePath + "/others/{profileId}", otherProfileId)
					.param("order", "like")
					.header(AUTHORIZATION, token)
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks[0].id").value(bookmarkId1),
					jsonPath("$.bookmarks[0].likeCount").value(1),
					jsonPath("$.bookmarks[1].id").value(bookmarkId4),
					jsonPath("$.bookmarks[1].likeCount").value(0));
		}
	}

	@Nested
	class 피드_북마크_조회 {

		//  사용자 1 			-팔로우->	사용자 2 				사용자 3
		// bookmark4 all,    		bookmark7 all, 		bookmark10 all
		// bookmark5 partial,		bookmark8 partial	bookmark11 partial
		// bookmark6 private,       bookmark9 private   bookmark12 private

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
