package com.meoguri.linkocean.controller.linkmetadata;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

import com.meoguri.linkocean.controller.BaseControllerTest;

class LinkMetadataControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(LinkMetadataController.class);

	@WithMockUser(roles = "USER")
	@Test
	void 링크메타데이터_제목_조회_Api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", "GOOGLE");
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		final String link = "http://www.naver.com";
		//when
		mockMvc.perform(post(UriComponentsBuilder.fromUriString(basePath)
				.pathSegment("obtain")
				.queryParam("link", link)
				.build()
				.toUri())
				.session(session)
				.contentType(MediaType.APPLICATION_JSON))

			//then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").exists())
			.andDo(print());

	}

}
