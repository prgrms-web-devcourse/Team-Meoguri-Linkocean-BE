package com.meoguri.linkocean.controller.profile;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.apache.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class FavoriteControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(FavoriteController.class);

	@Test
	void 즐겨찾기_추가_Api_성공() throws Exception {
		//given
		유저_등록_로그인("haha@gmail.com", GOOGLE);
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{bookmarkId}/favorite", bookmarkId)
			.header(AUTHORIZATION, token)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void 즐겨찾기_추가_Api_실패_두번_연속_호출() throws Exception {
		//given
		유저_등록_로그인("haha@gmail.com", GOOGLE);
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");
		북마크_즐겨찾기(bookmarkId);

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{bookmarkId}/favorite", bookmarkId)
			.header(AUTHORIZATION, token)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
}
