package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Map;

import com.meoguri.linkocean.domain.user.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
 */
@Getter
@RequiredArgsConstructor
public class OAuthAttributes {

	private final Map<String, Object> attributes;
	private final String email;
	private final String oAuthType;

	public static OAuthAttributes of(final String registrationId, final Map<String, Object> attributes) {
		return SecurityOAuthType.of(registrationId).toOAuthAttributes.apply(attributes);
	}

	public User toEntity() {
		return new User(email, oAuthType);
	}
}
