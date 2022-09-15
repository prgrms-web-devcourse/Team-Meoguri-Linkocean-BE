package com.meoguri.linkocean.infrastructure.oauth.google;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.service.OAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleOAuthService implements OAuthService {

	private static final String CODE = "code";
	private static final String TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
	private static final String USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

	private final GoogleOAuthProperties googleOAuthProperties;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public String getRedirectUrl() {
		Map<String, Object> params = new HashMap<>();
		params.put("scope", googleOAuthProperties.getScope());
		params.put("response_type", CODE);
		params.put("client_id", googleOAuthProperties.getClientId());
		params.put("redirect_uri", googleOAuthProperties.getRedirectUrl());

		String redirectUrl = makeRedirectUrl(params);
		log.info("google redirect url : %s", redirectUrl);

		return redirectUrl;
	}

	private String makeRedirectUrl(final Map<String, Object> params) {
		String parameterString = params.entrySet().stream()
			.map(x -> x.getKey() + "=" + x.getValue())
			.collect(Collectors.joining("&"));
		return googleOAuthProperties.getUrl() + "?" + parameterString;
	}

	@Override
	public String getAccessToken(final String authorizationCode) throws JsonProcessingException {

		final HashMap<String, Object> params = new HashMap<>();
		params.put("code", authorizationCode);
		params.put("client_id", googleOAuthProperties.getClientId());
		params.put("client_secret", googleOAuthProperties.getClientSecret());
		params.put("grant_type", googleOAuthProperties.getGrantType());
		params.put("redirect_uri", googleOAuthProperties.getRedirectUrl());

		final ResponseEntity<String> responseEntity = restTemplate.postForEntity(TOKEN_REQUEST_URL, params,
			String.class);

		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			throw new IllegalArgumentException("access token을 응답 받지 못했습니다.");
		}

		return convertToGoogleAuthToken(responseEntity.getBody()).getAccess_token();
	}

	private GoogleOAuthToken convertToGoogleAuthToken(final String body) throws JsonProcessingException {
		return objectMapper.readValue(body, GoogleOAuthToken.class);
	}

	@Override
	public Email getUserEmail(String accessToken) throws JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(
			USERINFO_REQUEST_URL,
			HttpMethod.GET,
			request,
			String.class);

		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			throw new IllegalArgumentException("사용자 정보를 가져오는데 실패했습니다.");
		}

		log.info("responseEntity.getBody() = " + responseEntity.getBody());
		return new Email(convertToGoogleUser(responseEntity.getBody()).getEmail());
	}

	private GoogleUser convertToGoogleUser(String body) throws JsonProcessingException {
		return objectMapper.readValue(body, GoogleUser.class);
	}
}
