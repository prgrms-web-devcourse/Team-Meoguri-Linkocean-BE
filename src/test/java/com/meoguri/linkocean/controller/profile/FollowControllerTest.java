package com.meoguri.linkocean.controller.profile;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;

class FollowControllerTest extends BaseControllerTest {

	private final String baseUrl = getBaseUrl(FollowController.class);

	long hahaId;

	@BeforeEach
	void setUp() throws Exception {
		//given - 두 사용자 haha 와 papa
		유저_등록_로그인("haha@gmail.com", "GOOGLE");
		hahaId = 프로필_등록("haha", List.of("IT"));

		유저_등록_로그인("papa@gmail.com", "GOOGLE");
		프로필_등록("papa", List.of("자기계발"));
	}

	@Test
	void 팔로우_Api_성공() throws Exception {
		//when
		mockMvc.perform(post(baseUrl + "/follow")
				.param("followeeId", String.valueOf(hahaId))
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andExpect(status().isOk());

		final GetDetailedProfileResponse hahaProfile = 프로필_상세_조회(hahaId);
		final GetMyProfileResponse papaProfile = 내_프로필_조회();

		assertThat(hahaProfile.getFollowerCount()).isEqualTo(1L);
		assertThat(hahaProfile.getFolloweeCount()).isEqualTo(0L);

		assertThat(papaProfile.getFollowerCount()).isEqualTo(0L);
		assertThat(papaProfile.getFolloweeCount()).isEqualTo(1L);
	}

	@Test
	void 팔로우_두번_연속_실패() throws Exception {
		//given
		팔로우(hahaId);

		//when
		mockMvc.perform(post(baseUrl + "/follow")
				.param("followeeId", String.valueOf(hahaId))
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andExpect(status().isBadRequest());
	}

	@Test
	void 언팔로우_Api_성공() throws Exception {
		//given
		팔로우(hahaId);

		//when
		mockMvc.perform(post(baseUrl + "/unfollow")
				.param("followeeId", String.valueOf(hahaId))
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andExpect(status().isOk());

		final GetDetailedProfileResponse hahaProfile = 프로필_상세_조회(hahaId);
		final GetMyProfileResponse papaProfile = 내_프로필_조회();

		assertThat(hahaProfile.getFollowerCount()).isEqualTo(0L);
		assertThat(hahaProfile.getFolloweeCount()).isEqualTo(0L);

		assertThat(papaProfile.getFollowerCount()).isEqualTo(0L);
		assertThat(papaProfile.getFolloweeCount()).isEqualTo(0L);
	}

	@Test
	void 팔로우_안하고_언팔로우_하면_실패() throws Exception {
		//when
		mockMvc.perform(post(baseUrl + "/unfollow")
				.param("followeeId", String.valueOf(hahaId))
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))

			//then
			.andExpect(status().isBadRequest());
	}
}
