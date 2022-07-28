package com.meoguri.linkocean.configuration.security.oauth;

import static lombok.AccessLevel.*;

import java.util.Map;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class OAuthAttributes {

	private final Map<String, Object> attributes;
	private final String email;
	private final OAuthType oAuthType;

	public static OAuthAttributes of(final String registrationId, final Map<String, Object> attributes) {
		if ("kakao".equals(registrationId)) {
			return ofKakao(attributes);
		}
		if ("naver".equals(registrationId)) {
			return ofNaver(attributes);
		}
		if ("google".equals(registrationId)) {
			return ofGoogle(attributes);
		}

		throw new IllegalStateException();

	}

	private static OAuthAttributes ofGoogle(final Map<String, Object> attributes) {
		return new OAuthAttributes(
			attributes,
			(String)attributes.get("email"),
			OAuthType.GOOGLE
		);
	}

	private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

		return new OAuthAttributes(
			attributes,
			(String)kakaoAccount.get("email"),
			OAuthType.KAKAO
		);
	}

	private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return new OAuthAttributes(
			response,
			(String)response.get("email"),
			OAuthType.NAVER
		);
	}

	public User toEntity() {
		return new User(email, oAuthType);
	}
}
