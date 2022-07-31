package com.meoguri.linkocean.controller.profile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.controller.BaseControllerTest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;

@Transactional
class ProfileControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(ProfileController.class);

	@WithMockUser(roles = "USER")
	@Test
	void 프로필_등록_Api_성공() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");

		final String username = "hani";
		final List<String> categories = List.of("ART", "SOCIAL", "IT");

		final CreateProfileRequest createProfileRequest = new CreateProfileRequest(username, categories);

		//when
		mockMvc.perform(post(basePath).session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson(createProfileRequest)))

			//then
			.andExpect(status().isOk())
			.andDo(print());
	}
}
