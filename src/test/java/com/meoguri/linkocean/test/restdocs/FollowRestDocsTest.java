package com.meoguri.linkocean.test.restdocs;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.controller.profile.FollowController;
import com.meoguri.linkocean.test.restdocs.support.RestDocsTestSupport;

class FollowRestDocsTest extends RestDocsTestSupport {

	private final String baseUrl = getBaseUrl(FollowController.class);

	long hahaId;

	@BeforeEach
	void setUp() throws Exception {
		/* given - 두 사용자 haha 와 papa */
		유저_등록_로그인("haha@gmail.com", GOOGLE);
		hahaId = 프로필_등록("haha", List.of("IT"));

		유저_등록_로그인("papa@gmail.com", GOOGLE);
		프로필_등록("papa", List.of("자기계발"));
	}

	@Test
	void 팔로우_api() throws Exception {
		//when
		final ResultActions perform = mockMvc.perform(post(baseUrl + "/follow")
			.param("followeeId", String.valueOf(hahaId))
			.header(AUTHORIZATION, token));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("followeeId").description("팔로위 ID")
					)
				)
			);
	}

	@Test
	void 언팔로우_api() throws Exception {
		//given
		팔로우(hahaId);

		//when
		final ResultActions perform = mockMvc.perform(post(baseUrl + "/unfollow")
			.param("followeeId", String.valueOf(hahaId))
			.header(AUTHORIZATION, token)
			.contentType(APPLICATION_JSON));

		//then
		perform
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("인증 토큰")
					),
					requestParameters(
						parameterWithName("followeeId").description("팔로위 ID")
					)
				)
			);
	}
}
