package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SecurityOAuthType {

	GOOGLE(attributes ->
		new OAuthAttributes(
			attributes,
			(String)attributes.get("email"),
			"GOOGLE"
		)
	),

	NAVER(attributes -> {
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return new OAuthAttributes(
			response,
			(String)response.get("email"),
			"NAVER"
		);
	}),

	KAKAO(attributes -> {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

		return new OAuthAttributes(
			kakaoAccount,
			(String)kakaoAccount.get("email"),
			"KAKAO"
		);
	});

	public final Function<Map<String, Object>, OAuthAttributes> toOAuthAttributes;

	public static SecurityOAuthType of(final String type) {
		try {
			return SecurityOAuthType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException();
		}
	}

}
