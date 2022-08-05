package com.meoguri.linkocean.controller.profile;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
					jsonPath("$.categories", hasItems("인문", "사회", "IT")))
				.andDo(print());
		}

		@Test
		void 내프로필_조회_사용자가_작성한_카테고리_조회_카테고리가_null_일때() throws Exception {
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

	@Nested
	class 프로필_목록_조회_시리즈_테스트 {
		long user1ProfileId;
		long user2ProfileId;
		long user3ProfileId;

		@BeforeEach
		void setUp() throws Exception {
			유저_등록_로그인("user1@gmail.com", "GOOGLE");
			user1ProfileId = 프로필_등록("user1", List.of("IT"));

			유저_등록_로그인("user2@gmail.com", "GOOGLE");
			user2ProfileId = 프로필_등록("user2", List.of("IT"));

			유저_등록_로그인("user3@gmail.com", "GOOGLE");
			user3ProfileId = 프로필_등록("user3", List.of("IT"));

			// 팔로우 화살표 : user1 <-> user2 -> user3
			로그인("user1@gmail.com", "GOOGLE");
			팔로우(user2ProfileId);

			로그인("user2@gmail.com", "GOOGLE");
			팔로우(user1ProfileId);
			팔로우(user3ProfileId);
		}

		// 1. 프로필 목록 조회 - 머구리 찾기 / 이름으로 필터링
		@Test
		void 프로필_목록_조회_Api_성공() throws Exception {
			로그인("user1@gmail.com", "GOOGLE");

			mockMvc.perform(get(basePath + "?username=" + "user")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles").isArray(),
					jsonPath("$.profiles", hasSize(3)),
					jsonPath("$.profiles[0].id").value(user1ProfileId),
					jsonPath("$.profiles[1].id").value(user2ProfileId),
					jsonPath("$.profiles[2].id").value(user3ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false),
					jsonPath("$.profiles[1].isFollow").value(true),
					jsonPath("$.profiles[2].isFollow").value(false)
				);
		}

		@Test
		void 프로필_목록_조회_Api_유저네임을_빠트리면_실패() throws Exception {
			로그인("user1@gmail.com", "GOOGLE");

			mockMvc.perform(get(basePath)
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		// 2. 팔로워 목록 조회
		/*
		팔로우 화살표 : user1 <-> user2 -> user3			user1 user2 user3
		user1 의 팔로워 : user2        user1 의 팔로우 여부    x      o     x
		user2 의 팔로워 : user1		 user2 의 팔로우 여부    o      x     o
		user3 의 팔로워 : user2		 user3 의 팔로우 여부    x      x     x
		 */
		@Test
		void 팔로워_조회_Api_성공() throws Exception {
			로그인("user1@gmail.com", "GOOGLE");

			// user1 -> user1 의 팔로워 조회
			mockMvc.perform(get(basePath + "/{profileId}", user1ProfileId)
					.param("tab", "follower")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].id").value(user2ProfileId),
					jsonPath("$.profiles[0].isFollow").value(true)
				);

			// user1 -> user2 의 팔로워 조회
			mockMvc.perform(get(basePath + "/{profileId}", user2ProfileId)
					.param("tab", "follower")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].id").value(user1ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false)
				);

			// user1 -> user3 의 팔로워 조회
			mockMvc.perform(get(basePath + "/{profileId}", user3ProfileId)
					.param("tab", "follower")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].id").value(user2ProfileId),
					jsonPath("$.profiles[0].isFollow").value(true)
				);
		}

		// 3. 팔로이 목록 조회
		/*
		팔로우 화살표 : user1 <-> user2 -> user3	       		user1 user2 user3
		user1 의 팔로이 : user2            user1 의 팔로우 여부    x      o     x
		user2 의 팔로이 : user1, user3	 user2 의 팔로우 여부    o      x     o
		user3 의 팔로이 : x				 user3 의 팔로우 여부    x      x     x
		 */
		@Test
		void 팔로이_조회_Api_성공() throws Exception {
			로그인("user1@gmail.com", "GOOGLE");

			// user1 -> user1 의 팔로이 조회
			mockMvc.perform(get(basePath + "/{profileId}", user1ProfileId)
					.param("tab", "followee")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].id").value(user2ProfileId),
					jsonPath("$.profiles[0].isFollow").value(true)
				);

			// user1 -> user2 의 팔로이 조회
			mockMvc.perform(get(basePath + "/{profileId}", user2ProfileId)
					.param("tab", "followee")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(2)),
					jsonPath("$.profiles[0].id").value(user1ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false),
					jsonPath("$.profiles[1].id").value(user3ProfileId),
					jsonPath("$.profiles[1].isFollow").value(false)
				);

			// user1 -> user3 의 팔로이 조회
			mockMvc.perform(get(basePath + "/{profileId}", user3ProfileId)
					.param("tab", "followee")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.profiles", hasSize(0)));
		}

		@Test
		void 팔로워_팔로이_조회_탭을_누락하면_실패() throws Exception {
			mockMvc.perform(get(basePath + "/" + user2ProfileId)
					.param("tab", "hi")
					.session(session)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}
	}
}
