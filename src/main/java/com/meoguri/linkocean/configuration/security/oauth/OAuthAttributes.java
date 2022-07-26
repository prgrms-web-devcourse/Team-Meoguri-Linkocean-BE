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
		return ofGoogle(attributes);
	}

	private static OAuthAttributes ofGoogle(final Map<String, Object> attributes) {
		return new OAuthAttributes(
			attributes,
			(String)attributes.get("email"),
			OAuthType.GOOGLE
		);
	}

	public User toEntity() {
		return new User(email, oAuthType);
	}
}
