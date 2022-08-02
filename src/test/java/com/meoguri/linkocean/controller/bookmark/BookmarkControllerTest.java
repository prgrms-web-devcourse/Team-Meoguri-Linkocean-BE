package com.meoguri.linkocean.controller.bookmark;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;

class BookmarkControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(BookmarkController.class);

	@WithMockUser(roles = "USER")
	@Test
	void 북마크_등록_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));
		final String link = 링크_메타데이터_조회("http://www.naver.com");

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
			.andDo(print());
	}

}
