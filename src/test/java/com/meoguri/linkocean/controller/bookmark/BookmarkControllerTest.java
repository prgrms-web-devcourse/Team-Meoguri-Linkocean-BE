package com.meoguri.linkocean.controller.bookmark;

import static java.time.format.DateTimeFormatter.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.service.BookmarkService;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;

class BookmarkControllerTest extends BaseControllerTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private final String basePath = getBaseUrl(BookmarkController.class);

	@Test
	void 북마크_등록_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));
		final String link = 링크_메타데이터_얻기("http://www.naver.com");

		final String title = "title1";
		final String memo = "memo";
		final String category = "인문";
		final String openType = "private";

		final RegisterBookmarkRequest registerBookmarkRequest =
			new RegisterBookmarkRequest(link, title, memo, category, openType, null);

		//when
		mockMvc.perform(post(basePath).session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson(registerBookmarkRequest)))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andDo(print());
	}

	@Test
	void 제목_메모_카테고리_없는_북마크_상세_조회_Api_성공() throws Exception {
		//given
		유저_등록_로그인("crush@gmail.com", "GOOGLE");
		프로필_등록("crush", List.of("IT", "인문"));

		링크_메타데이터_얻기("http://www.naver.com");

		final Long userId = ((SessionUser)session.getAttribute("user")).getId();

		final long savedBookmarkId = bookmarkService.registerBookmark(
			new RegisterBookmarkCommand(
				userId,
				"https://www.naver.com",
				null,
				null,
				null,
				"all",
				List.of("tag1", "tag2"))
		);

		Bookmark savedBookmark = bookmarkRepository.findByIdFetchProfileAndLinkMetadataAndTags(savedBookmarkId).get();

		//when then
		mockMvc.perform(get(basePath + "/" + savedBookmarkId).session(session))
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

}
