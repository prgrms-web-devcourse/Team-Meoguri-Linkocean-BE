package com.meoguri.linkocean.controller.bookmark;

import static java.time.format.DateTimeFormatter.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;

class BookmarkControllerTest extends BaseControllerTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private final String basePath = getBaseUrl(BookmarkController.class);

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));
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
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson(registerBookmarkRequest)))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andDo(print());
	}

	@Test
	void 북마크_삭제_Api_성공() throws Exception {

		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		// 북마크 삭제
		//when
		mockMvc.perform(delete(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andDo(print());

		// 북마크 조회 -> LinkedOceanRuntimcException 발생, 북마크가 존재하지 않음
		mockMvc.perform(get(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isBadRequest());
	}

	@Test
	void 제목_메모_카테고리_없는_북마크_상세_조회_Api_성공() throws Exception {
		//given
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("good", "spring"), "all");

		Bookmark savedBookmark = bookmarkRepository.findByIdFetchProfileAndLinkMetadataAndTags(bookmarkId).get();

		//when then
		mockMvc.perform(get(basePath + "/" + bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.title").value(savedBookmark.getTitle()),
				jsonPath("$.url").value(savedBookmark.getUrl()),
				jsonPath("$.imageUrl").value(savedBookmark.getLinkMetadata().getImage()),
				jsonPath("$.category").value(savedBookmark.getCategory()),
				jsonPath("$.memo").value(savedBookmark.getMemo()),
				jsonPath("$.openType").value(savedBookmark.getOpenType()),
				jsonPath("$.isFavorite").value(false),
				jsonPath("$.updatedAt").value(savedBookmark.getUpdatedAt().format(ofPattern("yyyy-MM-dd"))),
				jsonPath("$.tags").isArray(),
				jsonPath("$.tags", hasSize(savedBookmark.getTagNames().size())),
				jsonPath("$.tags[0]").value(savedBookmark.getTagNames().get(0)),
				jsonPath("$.tags[1]").value(savedBookmark.getTagNames().get(1)),
				jsonPath("$.reactionCount.like").value(0),
				jsonPath("$.reactionCount.hate").value(0),
				jsonPath("$.reaction.like").value(false),
				jsonPath("$.reaction.hate").value(false),
				jsonPath("$.profile.profileId").value(savedBookmark.getProfile().getId()),
				jsonPath("$.profile.username").value(savedBookmark.getProfile().getUsername()),
				jsonPath("$.profile.imageUrl").value(savedBookmark.getProfile().getImage()),
				jsonPath("$.profile.isFollow").value(false)
			).andDo(print());
	}

	@Nested
	class 내_북마크_목록_조회_테스트 {

		private long bookmarkId1;
		private long bookmarkId2;

		@Autowired
		private FindBookmarkByIdQuery findBookmarkByIdQuery;

		@BeforeEach
		void setUp() throws Exception {
			bookmarkId1 = 북마크_등록(링크_메타데이터_얻기("https://www.naver.com"), "IT", List.of("공부"), "all");
			bookmarkId2 = 북마크_등록(링크_메타데이터_얻기("https://www.airbnb.co.kr"), "여행", List.of("travel"), "partial");
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_필터링_조건_없이_조회() throws Exception {
			//given
			final Bookmark bookmark1 = findBookmarkByIdQuery.findById(bookmarkId1);
			final Bookmark bookmark2 = findBookmarkByIdQuery.findById(bookmarkId2);

			//when then
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2),
					jsonPath("$.bookmarks", hasSize(2)),
					jsonPath("$.bookmarks[0].id").value(bookmark2.getId()),
					jsonPath("$.bookmarks[0].title").value(bookmark2.getTitle()),
					jsonPath("$.bookmarks[0].url").value(bookmark2.getUrl()),
					jsonPath("$.bookmarks[0].openType").value(bookmark2.getOpenType()),
					jsonPath("$.bookmarks[0].updatedAt").value(bookmark2.getUpdatedAt().toLocalDate().toString()),
					jsonPath("$.bookmarks[0].imageUrl").value(bookmark2.getLinkMetadata().getImage()),
					jsonPath("$.bookmarks[0].likeCount").value(0),
					jsonPath("$.bookmarks[0].isFavorite").value(false),
					jsonPath("$.bookmarks[0].isWriter").value(true),
					jsonPath("$.bookmarks[0].tags", hasSize(1)),
					jsonPath("$.bookmarks[0].tags[0]").value(bookmark2.getTagNames().get(0)),
					jsonPath("$.bookmarks[1].id").value(bookmark1.getId())
				).andDo(print());
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_카테고리_필터링() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.param("category", "IT")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].category").value("IT")
				)
				.andDo(print());
		}

		//TODO BaseController에 즐겨찾기 추가 메서드 만들어서 사용하기
		@Disabled
		@Test
		void 내_북마크_목록_조회_Api_성공_즐겨찾기_필터링() throws Exception {
			//given
			//favoriteService.favorite(userId, bookmarkId1);

			//when then
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.param("favorite", "true")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(1),
					jsonPath("$.bookmarks[0].isFavorite").value(true)
				).andDo(print());
		}

		@Test
		void 내_북마크_목록_조회_Api_성공_태그_필터링() throws Exception {
			//when then
			mockMvc.perform(get(basePath + "/me")
					.header(AUTHORIZATION, token)
					.param("tags", "공부,travel")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.totalCount").value(2)
				).andDo(print());
		}
	}

	@Test
	void 다른_사람_북마크_조회_Api_더미_데이터_반환_테스트() throws Exception {
		//when then
		mockMvc.perform(get(basePath + "/others/1")
				.header(AUTHORIZATION, token)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
