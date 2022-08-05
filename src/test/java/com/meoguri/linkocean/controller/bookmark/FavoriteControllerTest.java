package com.meoguri.linkocean.controller.bookmark;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.controller.BaseControllerTest;

class FavoriteControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(FavoriteController.class);

	@Test
	void 즐겨찾기_추가() throws Exception {

		//given
		유저_등록_로그인("haha@gmail.com", "NAVER");
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");

		//when
		mockMvc.perform(post(basePath + "/{bookmarkId}/favorite", bookmarkId)
			.session(session)
			.contentType(MediaType.APPLICATION_JSON))

				//then
				.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	void 즐겨찾기_추가_실패() throws Exception {

		//given
		유저_등록_로그인("haha@gmail.com", "NAVER");
		프로필_등록("haha", List.of("인문", "정치", "사회", "IT"));
		final long bookmarkId = 북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("tag1", "tag2"), "all");
		즐겨찾기에_추가(bookmarkId);

		//when
		mockMvc.perform(post(basePath + "/{bookmarkId}/favorite", bookmarkId)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON))
			//then
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
}
