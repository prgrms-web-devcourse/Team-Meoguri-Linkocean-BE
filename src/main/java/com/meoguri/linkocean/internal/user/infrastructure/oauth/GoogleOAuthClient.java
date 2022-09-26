package com.meoguri.linkocean.internal.user.infrastructure.oauth;

import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.exception.OAuthException;
import com.meoguri.linkocean.internal.user.application.OAuthClient;
import com.meoguri.linkocean.internal.user.domain.model.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleOAuthClient implements OAuthClient {

	private final GoogleOAuthProperties googleOAuthProperties;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	/* 테스트 용도 */
	@Deprecated
	@Override
	public String getAuthorizationUri() {

		String authorizationUri = UriComponentsBuilder.fromHttpUrl(googleOAuthProperties.getAuthorizationUri())
			.queryParam("scope", googleOAuthProperties.getScope())
			.queryParam("response_type", googleOAuthProperties.getResponseType())
			.queryParam("client_id", googleOAuthProperties.getClientId())
			.queryParam("redirect_uri", "https://localhost/api/v1/auth/google")
			.build().encode().toString();
		log.info("google authorization url : {}", authorizationUri);

		return authorizationUri;
	}

	@Override
	public String getAccessToken(final String authorizationCode, final String redirectUri) {

		final HashMap<String, Object> params = new HashMap<>();
		params.put("code", authorizationCode);
		params.put("client_id", googleOAuthProperties.getClientId());
		params.put("client_secret", googleOAuthProperties.getClientSecret());
		params.put("grant_type", googleOAuthProperties.getGrantType());
		params.put("redirect_uri", redirectUri);

		final ResponseEntity<String> responseEntity = restTemplate.postForEntity(
			googleOAuthProperties.getTokenUri(),
			params,
			String.class);

		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			throw new IllegalArgumentException("access token을 응답 받지 못했습니다.");
		}

		return convertToGoogleAuthToken(responseEntity.getBody()).getAccessToken();
	}

	private GoogleOAuthToken convertToGoogleAuthToken(final String body) {
		try {
			return objectMapper.readValue(body, GoogleOAuthToken.class);
		} catch (JsonProcessingException ex) {
			throw new OAuthException("구글 access token 파싱에 실패했습니다.");
		}
	}

	@Override
	public Email getUserEmail(String accessToken) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(
			googleOAuthProperties.getUserInfoUri(),
			HttpMethod.GET,
			request,
			String.class);

		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			throw new IllegalArgumentException("사용자 정보를 가져오는데 실패했습니다.");
		}

		log.info("responseEntity.getBody() = " + responseEntity.getBody());
		return new Email(convertToGoogleUser(responseEntity.getBody()).getEmail());
	}

	private GoogleUser convertToGoogleUser(String body) {
		try {
			return objectMapper.readValue(body, GoogleUser.class);
		} catch (JsonProcessingException ex) {
			throw new OAuthException("구글 사용자 정보 파싱에 실패했습니다.");
		}
	}
}
