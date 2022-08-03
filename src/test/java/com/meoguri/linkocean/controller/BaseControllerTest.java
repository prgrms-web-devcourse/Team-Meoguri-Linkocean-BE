package com.meoguri.linkocean.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Import(P6spyLogMessageFormatConfiguration.class)
public class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected MockHttpSession session;

	@Autowired
	private UserRepository userRepository;

	protected void 유저_등록_로그인(final String email, final String oAuthType) {
		final User savedUser = userRepository.save(new User(email, oAuthType));

		session = new MockHttpSession();
		session.setAttribute("user", new SessionUser(savedUser));
	}

	protected long 프로필_등록(final String username, final List<String> categories) throws Exception {
		final MvcResult mvcResult = mockMvc.perform(post("/api/v1/profiles")
				.session(session)
				.contentType(APPLICATION_JSON)
				.content(createJson(new CreateProfileRequest(username, categories))))
			.andExpect(status().isOk())
			.andReturn();

		return toId(mvcResult);
	}

	protected String 링크_메타데이터_조회(final String link) throws Exception {
		mockMvc.perform(post(UriComponentsBuilder.fromUriString("/api/v1/linkmetadatas/obtain")
				.queryParam("link", link)
				.build()
				.toUri())
				.session(session)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());

		return link;
	}

	protected long 북마크_등록(final String url, final String category, final List<String> tags,
		final String openType) throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(post("/api/v1/bookmarks")
					.session(session)
					.contentType(APPLICATION_JSON)
					.content(createJson(new RegisterBookmarkRequest(url, "title", "memo", category, openType, tags))))
				.andExpect(status().isOk())
				.andReturn();

		return toId(mvcResult);
	}

	protected void 팔로우(final long followeeId) throws Exception {
		mockMvc.perform(post("/api/v1/profiles/follow?followeeId=" + followeeId)
				.session(session)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

/*
	protected GetMyProfileResponse 프로필_조회(long profileId) throws Exception {

		final MvcResult mvcResult = mockMvc.perform(get("/api/v1/profiles/" + profileId)
				.session(session)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		final String content = mvcResult.getResponse().getContentAsString();

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(content, GetMyProfileResponse.class);
	}
*/

	protected GetMyProfileResponse 내_프로필_조회() throws Exception {

		final MvcResult mvcResult = mockMvc.perform(get("/api/v1/profiles/me")
				.session(session)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		final String content = mvcResult.getResponse().getContentAsString();

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(content, GetMyProfileResponse.class);
	}

	protected String createJson(Object dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	protected static String getBaseUrl(final Class<?> clazz) {

		return Stream.of(Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, RequestMapping.class))
				.map(RequestMapping::value)
				.orElseThrow(NullPointerException::new))
			.findFirst()
			.orElseThrow(NullPointerException::new);
	}

	private long toId(final MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
		final String content = mvcResult.getResponse().getContentAsString();

		return objectMapper.readValue(content, JsonNode.class).get("id").asLong();
	}
}
