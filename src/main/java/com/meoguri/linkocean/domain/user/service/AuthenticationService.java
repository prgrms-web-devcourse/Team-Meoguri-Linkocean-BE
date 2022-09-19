package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public interface AuthenticationService {

	/* oAuthType에 맞는 소셜 로그인 uri를 반환한다. */
	@Deprecated
	String getAuthorizationUri(OAuthType oAuthType);

	/* 사용자 인증하고 jwt 토큰을 발급한다. */
	String authenticate(OAuthType oAuthType, String authorizationCode);
}
