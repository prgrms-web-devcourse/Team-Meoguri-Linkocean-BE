package com.meoguri.linkocean.infrastructure.oauth.google;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpMethod.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.domain.user.model.Email;

@SpringBootTest
class GoogleOAuthClientTest {

	@Autowired
	private GoogleOAuthClient googleOAuthClient;

	@Autowired
	private GoogleOAuthProperties googleOAuthProperties;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestTemplate restTemplate;

	@Test
	void getAuthorizationUri_성공() {
		//given when
		final String authorizationUri = googleOAuthClient.getAuthorizationUri();

		//then
		assertThat(authorizationUri).isNotBlank();
	}

	@Test
	void access_token_발급_요청_성공() throws JsonProcessingException {
		//given
		final String authorizationCode = "code";
		final String redirectUri = "http://localhost/redirectUri";

		final String accessToken = "access token";
		final GoogleOAuthToken googleOAuthToken = new GoogleOAuthToken(
			accessToken,
			10,
			"scope",
			"tokenType",
			"idToken");

		final HashMap<String, Object> params = new HashMap<>();
		params.put("code", authorizationCode);
		params.put("client_id", googleOAuthProperties.getClientId());
		params.put("client_secret", googleOAuthProperties.getClientSecret());
		params.put("grant_type", googleOAuthProperties.getGrantType());
		params.put("redirect_uri", redirectUri);

		given(restTemplate.postForEntity(googleOAuthProperties.getTokenUri(), params, String.class))
			.willReturn(ResponseEntity.ok(objectMapper.writeValueAsString(googleOAuthToken)));

		//when
		final String responseAccessToken = googleOAuthClient.getAccessToken(authorizationCode, redirectUri);

		//then
		assertThat(accessToken).isEqualTo(responseAccessToken);
	}

	@Test
	void 사용자_이메일_정보_요청_성공() throws JsonProcessingException {
		//given
		final String email = "email@google.com";
		final GoogleUser googleUser = new GoogleUser("id", email, true, "picture");

		final String accessToken = "access token";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		given(restTemplate.exchange(googleOAuthProperties.getUserInfoUri(), GET, request, String.class))
			.willReturn(ResponseEntity.ok(objectMapper.writeValueAsString(googleUser)));

		//when
		final Email responseEmail = googleOAuthClient.getUserEmail(accessToken);

		//then
		assertThat(responseEmail).isEqualTo(new Email(email));
	}
}
