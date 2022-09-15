package com.meoguri.linkocean.domain.user.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

	private final OAuthService oAuthService;

	public String getRedirectUrl(final OAuthType oAuthType) {

		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("알 수 없는 소셜 로그인 입니다.");
		}

		return oAuthService.getRedirectUrl();
	}

	public Email authenticate(final OAuthType oAuthType, final String authorizationCode) throws
		JsonProcessingException {

		//TODO 벤더사 추가 쉽도록 확장성 있게 리팩토링하기
		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("알 수 없는 소셜 로그인 입니다.");
		}

		final String accessToken = oAuthService.getAccessToken(authorizationCode);
		return oAuthService.getUserEmail(accessToken);
	}
}
