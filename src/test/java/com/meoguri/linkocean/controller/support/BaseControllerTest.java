package com.meoguri.linkocean.controller.support;

import static java.util.Collections.*;
import static org.springframework.http.HttpHeaders.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;
import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.controller.bookmark.dto.GetDetailedBookmarkResponse;
import com.meoguri.linkocean.controller.bookmark.dto.RegisterBookmarkRequest;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.domain.linkmetadata.entity.Link;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
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

	@Autowired
	private JwtProvider jwtProvider;

	protected String token;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

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
		userRepository.save(new User(email, oAuthType));
		token = String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
	}

	protected void 로그인(final String email, final String oAuthType) {
		userRepository.findByEmailAndOAuthType(new Email(email), OAuthType.valueOf(oAuthType)).orElseThrow();
		token = String.format("Bearer %s", jwtProvider.generate(email, oAuthType));
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

	/* 테스트 속도를 위해 리포지토리로 처리 */
	protected String 링크_메타데이터_얻기(final String link) {
		linkMetadataRepository.findByLink(new Link(link)).ifPresentOrElse(
			LinkMetadata::getLink, // dummy consumer
			() -> linkMetadataRepository.save(new LinkMetadata(link, "title", "image"))
		);
		return link;
	}

	protected long 북마크_등록(final String url, final String openType) throws Exception {
		return 북마크_등록(url, "title", null, emptyList(), openType);
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

	protected GetDetailedProfileResponse 내_프로필_조회() throws Exception {
		final MvcResult mvcResult =
			mockMvc.perform(get("/api/v1/profiles/me")
					.header(AUTHORIZATION, token)
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), GetDetailedProfileResponse.class);
	}

	protected void 북마크_즐겨찾기(final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/favorite", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	protected void 북마크_좋아요(final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/reactions/like", bookmarkId)
				.header(AUTHORIZATION, token)
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	protected void 북마크_싫어요(final long bookmarkId) throws Exception {
		mockMvc.perform(post("/api/v1/bookmarks/{bookmarkId}/reactions/hate", bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk());
	}

	protected GetDetailedBookmarkResponse 북마크_상세_조회(final long bookmarkId) throws Exception {
		final MvcResult mvcResult = mockMvc.perform(get("/api/v1/bookmarks/{bookmarkId}", bookmarkId)
				.header(AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andReturn();

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		return mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), GetDetailedBookmarkResponse.class);
	}

	private long toId(final MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
		final String content = mvcResult.getResponse().getContentAsString();

		return objectMapper.readValue(content, JsonNode.class).get("id").asLong();
	}

}
