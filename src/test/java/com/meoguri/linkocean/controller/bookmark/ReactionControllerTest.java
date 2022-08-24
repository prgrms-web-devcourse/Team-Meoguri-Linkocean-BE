package com.meoguri.linkocean.controller.bookmark;

import static com.meoguri.linkocean.domain.profile.command.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.apache.http.HttpHeaders.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.bookmark.dto.GetDetailedBookmarkResponse;
import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class ReactionControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(ReactionController.class);

	private long bookmarkId;

	@BeforeEach
	void setUp() throws Exception {
		유저_등록_로그인("haha@gmail.com", NAVER);
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "IT", null, "all");
	}

	@Test
	void 리액션_좋아요() throws Exception {
		//when
		mockMvc.perform(post(basePath + "/{bookmarkId}/reactions/{reactionType}", bookmarkId, "like")
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andDo(print());

		//then
		final GetDetailedBookmarkResponse response = 북마크_상세_조회(bookmarkId);

		assertThat(response.getReactionCount())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, 1L,
				HATE, 0L
			));
		assertThat(response.getReaction())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, true,
				HATE, false
			));
	}

	@Test
	void 리액션_좋아요_싫어요() throws Exception {
		//when
		북마크_좋아요(bookmarkId);
		북마크_싫어요(bookmarkId);

		//then
		final GetDetailedBookmarkResponse response = 북마크_상세_조회(bookmarkId);

		assertThat(response.getReactionCount())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, 0L,
				HATE, 1L
			));
		assertThat(response.getReaction())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, false,
				HATE, true
			));
	}

	@Test
	void 리액션_좋아요_좋아요() throws Exception {
		//when
		북마크_좋아요(bookmarkId);
		북마크_좋아요(bookmarkId);

		//then
		final GetDetailedBookmarkResponse response = 북마크_상세_조회(bookmarkId);

		assertThat(response.getReactionCount())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, 0L,
				HATE, 0L
			));
		assertThat(response.getReaction())
			.containsExactlyInAnyOrderEntriesOf(Map.of(
				LIKE, false,
				HATE, false
			));
	}
}
