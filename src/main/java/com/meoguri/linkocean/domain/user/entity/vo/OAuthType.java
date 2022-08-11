package com.meoguri.linkocean.domain.user.entity.vo;

/**
 * OAuth 지원 벤더 종류
 * GOOGLE, NAVER, KAKAO
 */
public enum OAuthType {
	GOOGLE,
	NAVER,
	KAKAO;

	public static OAuthType of(final String type) {
		return OAuthType.valueOf(type);
	}

}
