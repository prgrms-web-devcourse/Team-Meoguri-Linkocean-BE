package com.meoguri.linkocean.controller.linkmetadata;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.meoguri.linkocean.support.controller.BaseControllerTest;

class LinkMetadataControllerTest extends BaseControllerTest {

	private final String basePath = getBaseUrl(LinkMetadataController.class);

	@Test
	void 링크메타데이터_제목_조회_Api() throws Exception {
		//given
		유저_등록_로그인("hani@gmail.com", GOOGLE);
		프로필_등록("hani", List.of("정치", "인문", "사회"));

		final String link = "http://www.naver.com";
		final Map<String, String> request = Map.of("url", link);

		//when
		mockMvc.perform(post(basePath + "/obtain")
				.header(AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").exists())
			.andDo(print());
	}

}
