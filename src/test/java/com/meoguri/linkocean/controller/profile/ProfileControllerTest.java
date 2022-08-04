package com.meoguri.linkocean.controller.profile;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;

class ProfileControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(ProfileController.class);

	@Test
	void 프로필_등록_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		final String username = "hani";
		final List<String> categories = List.of("인문", "정치", "사회");
		final CreateProfileRequest createProfileRequest = new CreateProfileRequest(username, categories);

		//when
		mockMvc.perform(post(basePath)
				.session(session)
				.contentType(APPLICATION_JSON)
				.content(createJson(createProfileRequest)))
			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andDo(print());
	}

	/*
	저희 내 프로필 조회에서 내 Tag 목록과 내가 작성한 카테고리 목록 모두 작성하기로 했습니다.

	아래의 내용들은 아직 API가 개발되지 않아서 추후 테스트를 보강하도록 하겠습니다.
	1. 내가 작성한 카터고리 목록 - 완료
	2. 내 Tag 목록
	3. 팔로워, 팔로위 수
	4. Bio, imageUrl
	 */
	@Nested
	class 내_프로필_조회_테스트 {

		@Test
		void 내프로필_조회_단순_프로필_정보_조회() throws Exception {
			//given
			유저_등록_로그인("hani@gmail.com", "GOOGLE");
			프로필_등록("hani", List.of("인문", "정치", "사회"));

			//when
			mockMvc.perform(get(basePath + "/me")
					.session(session)
					.contentType(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profileId").exists(),
					jsonPath("$.imageUrl").isEmpty(),
					jsonPath("$.favoriteCategories", hasSize(3)),
					jsonPath("$.username").value("hani"),
					jsonPath("$.bio").isEmpty(),
					jsonPath("$.followerCount").value(0),
					jsonPath("$.followeeCount").value(0),
					jsonPath("$.tags", hasSize(0)),
					jsonPath("$.categories", hasSize(0))
				)
				.andDo(print());
		}

		@Test
		void 내프로필_조회_사용자가_작성한_카테고리_조회() throws Exception {
			//given
			유저_등록_로그인("hani@gmail.com", "GOOGLE");
			프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));

			북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", null, "private");
			북마크_등록(링크_메타데이터_얻기("https://jojoldu.tistory.com"), "사회", null, "all");
			북마크_등록(링크_메타데이터_얻기("https://github.com"), "IT", null, "private");

			//when
			mockMvc.perform(get(basePath + "/me")
					.session(session)
					.contentType(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profileId").exists(),
					jsonPath("$.imageUrl").isEmpty(),
					jsonPath("$.favoriteCategories", hasSize(4)),
					jsonPath("$.username").value("hani"),
					jsonPath("$.bio").isEmpty(),
					jsonPath("$.followerCount").value(0),
					jsonPath("$.followeeCount").value(0),
					jsonPath("$.tags", hasSize(0)),
					jsonPath("$.categories", hasSize(3)),
					jsonPath("$.categories[0]").value("인문"),
					jsonPath("$.categories[1]").value("사회"),
					jsonPath("$.categories[2]").value("IT"))
				.andDo(print());
		}

		@Test
		void 내프로필_조회_사용자가_작성한_카테고리_조회_카테고리가_null일때() throws Exception {
			//given
			유저_등록_로그인("hani@gmail.com", "GOOGLE");
			프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));

			북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, null, "private");

			//when
			mockMvc.perform(get(basePath + "/me")
					.session(session)
					.contentType(APPLICATION_JSON))
				//then
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profileId").exists(),
					jsonPath("$.imageUrl").isEmpty(),
					jsonPath("$.favoriteCategories", hasSize(4)),
					jsonPath("$.username").value("hani"),
					jsonPath("$.bio").isEmpty(),
					jsonPath("$.followerCount").value(0),
					jsonPath("$.followeeCount").value(0),
					jsonPath("$.tags", hasSize(0)),
					jsonPath("$.categories", hasSize(0))
				)
				.andDo(print());
		}
	}
}
