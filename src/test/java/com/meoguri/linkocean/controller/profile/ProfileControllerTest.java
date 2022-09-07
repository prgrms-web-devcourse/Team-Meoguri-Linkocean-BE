package com.meoguri.linkocean.controller.profile;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class ProfileControllerTest extends BaseControllerTest {

	private final String baseUrl = getBaseUrl(ProfileController.class);

	@Test
	void 프로필_등록_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		final String username = "hani";
		final List<String> categories = List.of("인문", "정치", "사회");
		final CreateProfileRequest createProfileRequest = new CreateProfileRequest(username, categories);

		//when
		mockMvc.perform(post(baseUrl)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(createProfileRequest)))
			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andDo(print());
	}

	@Nested
	class 내_프로필_조회_테스트 {

		@Test
		void 내프로필_조회_단순_프로필_정보_조회() throws Exception {
			//given
			유저_등록_로그인("hani@gmail.com", GOOGLE);
			프로필_등록("hani", List.of("인문", "정치", "사회"));

			//when
			mockMvc.perform(get(baseUrl + "/me")
					.header(AUTHORIZATION, token)
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
			유저_등록_로그인("hani@gmail.com", GOOGLE);
			프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));

			북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", null, "private");
			북마크_등록(링크_메타데이터_얻기("https://jojoldu.tistory.com"), "사회", null, "all");
			북마크_등록(링크_메타데이터_얻기("https://github.com"), "IT", null, "private");

			//when
			mockMvc.perform(get(baseUrl + "/me")
					.header(AUTHORIZATION, token)
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
			유저_등록_로그인("hani@gmail.com", GOOGLE);
			프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));

			북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), null, null, "private");

			//when
			mockMvc.perform(get(baseUrl + "/me")
					.header(AUTHORIZATION, token)
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

	@Test
	void 프로필_상세_조회_Api_성공() throws Exception {
		//given
		유저_등록_로그인("user1@gmail.com", GOOGLE);
		final long user1ProfileId = 프로필_등록("user1", List.of("IT"));

		유저_등록_로그인("user2@gmail.com", GOOGLE);
		final long user2ProfileId = 프로필_등록("user2", List.of("IT"));

		로그인("user1@gmail.com", GOOGLE);
		팔로우(user2ProfileId);

		//when
		mockMvc.perform(get(baseUrl + "/{profileId}", user2ProfileId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			//then
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.isFollow").value(true),
				jsonPath("$.followerCount").value(1),
				jsonPath("$.followeeCount").value(0)
			).andDo(print());
	}

	@Test
	void 내프로필_수정_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));

		final String updateUsername = "updateHani";
		final List<String> updateCategories = List.of("자기계발", "과학");
		final String bio = "i like programming";
		final MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "image/png",
			"image".getBytes());

		// 내 프로필 수정
		//when
		mockMvc.perform(multipart(PUT, URI.create(baseUrl + "/me"))
				.file(mockImage)
				.param("username", updateUsername)
				.param("categories", "자기계발", "과학")
				.param("bio", bio)
				.header(AUTHORIZATION, token))
			//then
			.andExpect(status().isOk())
			.andDo(print());

		// 수정된 프로필 조회
		//when
		final GetDetailedProfileResponse myProfile = 내_프로필_조회();

		//then
		assertThat(myProfile.getUsername()).isEqualTo(updateUsername);
		assertThat(myProfile.getFavoriteCategories()).containsExactlyInAnyOrder("자기계발", "과학");
		assertThat(myProfile.getBio()).isEqualTo(bio);
		assertThat(myProfile.getImageUrl()).isNotNull();
	}

	@Nested
	class 프로필_목록_조회_시리즈_테스트 {
		long user1ProfileId;
		long user2ProfileId;
		long user3ProfileId;

		@BeforeEach
		void setUp() throws Exception {
			유저_등록_로그인("user1@gmail.com", GOOGLE);
			user1ProfileId = 프로필_등록("user1", List.of("IT"));

			유저_등록_로그인("user2@gmail.com", GOOGLE);
			user2ProfileId = 프로필_등록("user2", List.of("IT"));

			유저_등록_로그인("user3@gmail.com", GOOGLE);
			user3ProfileId = 프로필_등록("user3", List.of("IT"));

			// 팔로우 화살표 : user1 <-> user2 -> user3
			로그인("user1@gmail.com", GOOGLE);
			팔로우(user2ProfileId);

			로그인("user2@gmail.com", GOOGLE);
			팔로우(user1ProfileId);
			팔로우(user3ProfileId);
		}

		@Test
		void 유저네임으로_프로필_목록_조회_Api_성공() throws Exception {
			로그인("user1@gmail.com", GOOGLE);

			mockMvc.perform(get(baseUrl + "?username=" + "user")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.hasNext").value(false),
					jsonPath("$.profiles").isArray(),
					jsonPath("$.profiles", hasSize(3)),
					jsonPath("$.profiles[0].profileId").value(user3ProfileId),
					jsonPath("$.profiles[1].profileId").value(user2ProfileId),
					jsonPath("$.profiles[2].profileId").value(user1ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false),
					jsonPath("$.profiles[1].isFollow").value(true),
					jsonPath("$.profiles[2].isFollow").value(false)
				);
		}

		@Test
		void 프로필_목록_조회_Api_유저네임을_빠트리면_실패() throws Exception {
			로그인("user1@gmail.com", GOOGLE);

			mockMvc.perform(get(baseUrl)
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(print());
		}

		// 2. 팔로워 목록 조회
		/*
		팔로우 user1 의 팔로워 : user2화살표 : user1 <-> user2 -> user3			user1 user2 user3
		user1 의 팔로워 : user2        user1 의 팔로우 여부    x      o     x
		user2 의 팔로워 : user1		 user2 의 팔로우 여부    o      x     o
		user3 의 팔로워 : user2		 user3 의 팔로우 여부    x      x     x
		 */
		@Test
		void 팔로워_조회_Api_성공() throws Exception {
			로그인("user1@gmail.com", GOOGLE);

			// user1 -> user1 의 팔로워 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user1ProfileId, "follower")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.hasNext").value(false),
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].profileId").value(user2ProfileId),
					jsonPath("$.profiles[0].isFollow").value(true)
				);

			// user1 -> user2 의 팔로워 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user2ProfileId, "follower")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.hasNext").value(false),
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].profileId").value(user1ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false)
				);

			// user1 -> user3 의 팔로워 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user3ProfileId, "follower")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.hasNext").value(false),
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].profileId").value(user2ProfileId),
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
			로그인("user1@gmail.com", GOOGLE);

			// user1 -> user1 의 팔로이 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user1ProfileId, "followee")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(1)),
					jsonPath("$.profiles[0].profileId").value(user2ProfileId),
					jsonPath("$.profiles[0].isFollow").value(true)
				);

			// user1 -> user2 의 팔로이 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user2ProfileId, "followee")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(
					jsonPath("$.profiles", hasSize(2)),
					jsonPath("$.profiles[0].profileId").value(user3ProfileId),
					jsonPath("$.profiles[0].isFollow").value(false),
					jsonPath("$.profiles[1].profileId").value(user1ProfileId),
					jsonPath("$.profiles[1].isFollow").value(false)
				);

			// user1 -> user3 의 팔로이 조회
			mockMvc.perform(get(baseUrl + "/{profileId}/{tab}", user3ProfileId, "followee")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.profiles", hasSize(0)));
		}
	}
}
