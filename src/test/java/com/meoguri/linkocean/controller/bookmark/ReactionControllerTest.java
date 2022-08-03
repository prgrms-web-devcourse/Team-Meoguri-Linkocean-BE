package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.domain.bookmark.service.BookmarkService;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.profile.service.ProfileService;

class ReactionControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(ReactionController.class);

	@Autowired
	private BookmarkService bookmarkService;

	@WithMockUser(roles = "USER")
	@Test
	void 리액션_추가() throws Exception {

		//given
		유저_등록_로그인("haha@gmail.com", "NAVER");
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));

		/* "userId" from session */
		final Long userId = ((SessionUser)session.getAttribute("user")).getId();

		/* "bookmarkId" from bookmarkService */
		final long savedBookmarkId = bookmarkService.registerBookmark(
			new RegisterBookmarkCommand(userId, 링크_메타데이터_조회("http://www.naver.com"), "title", "memo", "인문", "all", List.of("tag1", "tag2"))
		);

		//when
		mockMvc.perform(post(basePath + "/{bookmarkId}/reactions/{reactionType}", savedBookmarkId, "like")
			.session(session)
			.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andDo(print());
	}
}
