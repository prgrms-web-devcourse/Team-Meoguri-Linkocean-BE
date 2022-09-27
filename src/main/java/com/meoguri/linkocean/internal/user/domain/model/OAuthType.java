package com.meoguri.linkocean.internal.user.domain.model;

/**
 * OAuth 지원 벤더 종류
 * GOOGLE, NAVER, KAKAO
 */
public enum OAuthType {
	GOOGLE,
	NAVER,
	KAKAO,
	GITHUB;

	public static OAuthType of(final String type) {
		return OAuthType.valueOf(type);
	}

}
