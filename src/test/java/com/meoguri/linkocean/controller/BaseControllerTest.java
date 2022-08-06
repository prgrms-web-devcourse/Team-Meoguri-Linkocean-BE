package com.meoguri.linkocean.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;
import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;
import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.User.OAuthType;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Import(P6spyLogMessageFormatConfiguration.class)
public class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	private JwtProvider jwtProvider;

	protected String token;

	@Autowired
	private UserRepository userRepository;

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

	protected void 유저_등록_로그인(final String email, final String oAuthType) {
		final User savedUser = userRepository.save(new User(email, oAuthType));

		token = String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
	}

	protected String 다른_유저_등록_로그인(final String email, final String oAuthType) {
		final User savedUser = userRepository.save(new User(email, oAuthType));
		return String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
	}

	protected void 로그인(final String email, final String oAuthType) {
		final User user = userRepository
			.findByEmailAndOAuthType(new Email(email), OAuthType.valueOf(oAuthType))
			.orElseThrow();

		token = String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
	}

	protected String 다른_유저_로그인(final String email, final String oAuthType) {
		final User user = userRepository
			.findByEmailAndOAuthType(new Email(email), OAuthType.valueOf(oAuthType))
			.orElseThrow();

		return String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
	}

	protected long 프로필_등록(final String username, final List<String> categories) throws Exception {
		final MvcResult mvcResult = mockMvc.perform(post("/api/v1/profiles")
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(new CreateProfileRequest(username, categories))))
			.andExpect(status().isOk())
			.andReturn();

		return toId(mvcResult);
	}

	protected long 다른_유저_프로필_등록(final String token, final String username, final List<String> categories) throws
		Exception {
		final MvcResult mvcResult = mockMvc.perform(post("/api/v1/profiles")
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON)
				.content(createJson(new CreateProfileRequest(username, categories))))
			.andExpect(status().isOk())
			.andReturn();

		return toId(mvcResult);
	}

	protected String 링크_메타데이터_얻기(final String link) throws Exception {
		mockMvc.perform(post("/api/v1/linkmetadatas/obtain")
				.param("link", link)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());

		return link;
	}

	protected long 북마크_등록(final String url, final String category, final List<String> tags,
		final String openType) throws Exception {
		return 북마크_등록(url, "title", category, tags, openType);
	}

	protected long 북마크_등록(final String url, final String title, final String category, final List<String> tags,
		final String openType) throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(post("/api/v1/bookmarks")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON)
					.content(createJson(new RegisterBookmarkRequest(url, title, "memo", category, openType, tags))))
				.andExpect(status().isOk())
				.andReturn();

		return toId(mvcResult);
	}

	protected long 다른_유저_북마크_등록(final String token, final long profileId, final String url, final String title,
		final String category,
		final List<String> tags, final String openType) throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(post("/api/v1/bookmarks")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON)
					.content(createJson(new RegisterBookmarkRequest(url, title, "memo", category, openType, tags))))
				.andExpect(status().isOk())
				.andReturn();

		return toId(mvcResult);
	}

	protected void 팔로우(final long followeeId) throws Exception {
		mockMvc.perform(post("/api/v1/profiles/follow")
				.param("followeeId", String.valueOf(followeeId))
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	protected GetDetailedProfileResponse 프로필_상세_조회(long profileId) throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(get("/api/v1/profiles/{profileId}", profileId)
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		final String content = mvcResult.getResponse().getContentAsString();

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(content, GetDetailedProfileResponse.class);
	}

	protected GetMyProfileResponse 내_프로필_조회() throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(get("/api/v1/profiles/me")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), GetMyProfileResponse.class);
	}

	protected void 북마크_즐겨찾기(final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/favorite", bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andDo(print());
	}

	protected void 다른_유저가_북마크_즐겨찾기(final String token, final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/favorite", bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andDo(print());
	}

	protected void 북마크_좋아요(final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/reactions/like", bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andDo(print());
	}

	private long toId(final MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
		final String content = mvcResult.getResponse().getContentAsString();

		return objectMapper.readValue(content, JsonNode.class).get("id").asLong();
	}

	// TODO - 리팩터링 제거 대상입니당
	protected long getUserId(final String tokenHeader) {
		String token = StringUtils.substringAfter(tokenHeader, "Bearer ");

		final String email = jwtProvider.getClaims(token, Claims::getId);
		final String oauthType = jwtProvider.getClaims(token, Claims::getAudience);

		return userRepository
			.findByEmailAndOAuthType(new Email(email), User.OAuthType.of(oauthType))
			.get().getId();

	}
}
