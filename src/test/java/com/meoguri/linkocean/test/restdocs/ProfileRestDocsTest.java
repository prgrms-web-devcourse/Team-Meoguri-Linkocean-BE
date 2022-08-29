package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.profile.ProfileController;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.test.support.controller.RestDocsTestSupport;

class ProfileRestDocsTest extends RestDocsTestSupport {

	private final String baseUrl = getBaseUrl(ProfileController.class);

	private long user1ProfileId;

	@Test
	void 프로필_등록_api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		final String username = "hani";
		final List<String> categories = List.of("인문", "정치", "사회");
		final CreateProfileRequest createProfileRequest = new CreateProfileRequest(username, categories);

		//when
		final ResultActions perform = mockMvc.perform(post(baseUrl)
			.header(AUTHORIZATION, token)
			.contentType(APPLICATION_JSON)
			.content(createJson(createProfileRequest)));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestFields(
						fieldWithPath("username").description("이메일"),
						fieldWithPath("categories").description("선호 카테고리")
					),
					responseFields(
						fieldWithPath("id").description("프로필 ID")
					)
				)
			);
	}

	@Test
	void 내_프로필_조회_api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("인문", "정치", "사회"));
		북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("스프링", "Spring Boot"), "private");

		//when
		final ResultActions perform = mockMvc.perform(get(baseUrl + "/me")
			.header(AUTHORIZATION, token));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					responseFields(
						fieldWithPath("profileId").description("프로필 ID"),
						fieldWithPath("imageUrl").optional().description("이미지 url"),
						fieldWithPath("favoriteCategories").description("선호 카테고리"),
						fieldWithPath("username").description("유저 이름"),
						fieldWithPath("bio").optional().description("자기 소개"),
						fieldWithPath("followerCount").description("팔로워 수"),
						fieldWithPath("followeeCount").description("팔로위 수"),
						fieldWithPath("isFollow").description("팔로우 여부"),
						fieldWithPath("tags[]").optional().description("사용자가 작성한 태그 리스트"),
						fieldWithPath("tags[].tag").optional().description("태그 이름"),
						fieldWithPath("tags[].count").optional().description("태그를 가진 북마크 수"),
						fieldWithPath("categories").optional().description("사용자가 작성한 게시글이 존재하는 카테고리")
					)
				)
			);
	}

	@Test
	void 다른_사람_프로필_조회_api() throws Exception {
		//given
		유저_등록_로그인("user1@gmail.com", GOOGLE);
		프로필_등록("user1", List.of("IT"));

		유저_등록_로그인("user2@gmail.com", GOOGLE);
		final long user2ProfileId = 프로필_등록("user2", List.of("IT"));
		북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("스프링", "Spring Boot"), "all");

		로그인("user1@gmail.com", GOOGLE);
		팔로우(user2ProfileId);

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.get(baseUrl + "/{profileId}", user2ProfileId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("profileId").description("프로필 ID")
					),
					responseFields(
						fieldWithPath("profileId").description("프로필 ID"),
						fieldWithPath("imageUrl").optional().optional().description("이미지 url"),
						fieldWithPath("favoriteCategories").description("선호 카테고리"),
						fieldWithPath("username").description("유저 이름"),
						fieldWithPath("bio").optional().optional().description("자기 소개"),
						fieldWithPath("followerCount").description("팔로워 수"),
						fieldWithPath("followeeCount").description("팔로위 수"),
						fieldWithPath("isFollow").description("팔로우 여부"),
						fieldWithPath("tags[]").optional().description("팔로우 여부"),
						fieldWithPath("tags[].tag").optional().description("태그 이름"),
						fieldWithPath("tags[].count").optional().description("태그를 가진 북마크 수"),
						fieldWithPath("categories").optional().description("사용자가 작성한 게시글이 있는 카테고리")
					)
				)
			);
	}

	@Test
	void 내_프로필_수정_api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("인문", "정치", "사회", "IT"));
		북마크_등록(링크_메타데이터_얻기("http://www.naver.com"), "인문", List.of("스프링", "Spring Boot"), "private");

		final String updateUsername = "updateHani";
		final String bio = "i like programming";
		final MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "image/png",
			"image".getBytes());

		//when
		final ResultActions perform = mockMvc.perform(multipart(PUT, URI.create(baseUrl + "/me"))
			.file(mockImage)
			.param("username", updateUsername)
			.param("categories", "자기계발", "과학")
			.param("bio", bio)
			.header(AUTHORIZATION, token));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("username").description("유저 이름"),
						parameterWithName("categories").description("선호 카테고리"),
						parameterWithName("bio").optional().description("자기 소개")
					),
					requestParts(
						partWithName("image").optional().description("이미지 파일")
					)
				)
			);
	}

	void internalSetUp() throws Exception {
		유저_등록_로그인("user1@gmail.com", GOOGLE);
		user1ProfileId = 프로필_등록("user1", List.of("IT"));

		유저_등록_로그인("user2@gmail.com", GOOGLE);
		long user2ProfileId = 프로필_등록("user2", List.of("IT"));

		유저_등록_로그인("user3@gmail.com", GOOGLE);
		long user3ProfileId = 프로필_등록("user3", List.of("IT"));

		/* 팔로우 화살표 : user1 <-> user2 -> user3 */
		로그인("user1@gmail.com", GOOGLE);
		팔로우(user2ProfileId);

		로그인("user2@gmail.com", GOOGLE);
		팔로우(user1ProfileId);
		팔로우(user3ProfileId);
	}

	@Test
	void 프로필_목록_조회_api() throws Exception {
		//given
		internalSetUp();
		로그인("user1@gmail.com", GOOGLE);

		//when
		final ResultActions perform = mockMvc.perform(get(baseUrl)
			.header(AUTHORIZATION, token)
			.param("username", "user")
			.contentType(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("프로필 개수(size)"),
						parameterWithName("username").optional().description("유저 JsonFieldType.이름")
					),
					responseFields(
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("profiles[]").optional().description("프로필 리스트"),
						fieldWithPath("profiles[].profileId").description("프로필 ID"),
						fieldWithPath("profiles[].username").description("유저 이름"),
						fieldWithPath("profiles[].imageUrl").optional().description("이미지 URL"),
						fieldWithPath("profiles[].isFollow").description("팔로우 여부")
					)
				)
			);
	}

	@Test
	void 팔로워_목록_조회_api() throws Exception {
		//given
		internalSetUp();
		로그인("user1@gmail.com", GOOGLE);

		//when
		final ResultActions perform = mockMvc.perform(
			RestDocumentationRequestBuilders.get(baseUrl + "/{profileId}/{tab}", user1ProfileId, "follower")
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("profileId").description("프로필 ID"),
						parameterWithName("tab").description("팔로우, 팔로위 토클")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("프로필 개수(size)"),
						parameterWithName("username").optional().description("유저 이름")
					),
					responseFields(
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("profiles[]").optional().description("프로필 리스트"),
						fieldWithPath("profiles[].profileId").description("프로필 ID"),
						fieldWithPath("profiles[].username").description("유저 이름"),
						fieldWithPath("profiles[].imageUrl").optional().description("이미지 URL"),
						fieldWithPath("profiles[].isFollow").description("팔로우 여부")
					)
				)
			);
	}

	@Test
	void 팔로이_목록_조회_api() throws Exception {
		//given
		internalSetUp();
		로그인("user2@gmail.com", GOOGLE);

		//when
		mockMvc.perform(
				RestDocumentationRequestBuilders.get(baseUrl + "/{profileId}/{tab}", user1ProfileId, "followee")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))

			//then
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					pathParameters(
						parameterWithName("profileId").description("프로필 ID"),
						parameterWithName("tab").description("팔로우, 팔로위 토클")
					),
					requestParameters(
						parameterWithName("page").optional().description("현재 페이지(page)"),
						parameterWithName("size").optional().description("프로필 개수(size)"),
						parameterWithName("username").optional().description("유저 이름")
					),
					responseFields(
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("profiles[]").optional().description("프로필 리스트"),
						fieldWithPath("profiles[].profileId").description("프로필 ID"),
						fieldWithPath("profiles[].username").description("유저 이름"),
						fieldWithPath("profiles[].imageUrl").optional().description("이미지 URL"),
						fieldWithPath("profiles[].isFollow").description("팔로우 여부")
					)
				)
			);
	}
}
