package com.meoguri.linkocean.controller.user;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.service.OAuthClient;
import com.meoguri.linkocean.test.support.controller.BaseControllerTest;

class AuthControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(AuthController.class);

	@MockBean
	private OAuthClient oAuthClient;

	@Test
	void 사용자_인증_api_성공() throws Exception {
		//given
		final String oAuthType = "google";
		given(oAuthClient.getUserEmail(any())).willReturn(new Email("email@google.com"));

		//when
		final ResultActions perform = mockMvc.perform(post(basePath + "/{oAuthType}", oAuthType)
			.param("code", "code")
			.accept(APPLICATION_JSON));

		//then
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists())
			.andDo(print());
	}
}