package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 커스텀 OAuth2 사용자 서비스
 * - 테스트는 index.html 을 띄우고 눈으로 확인해 주세요.
 */
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private static final Set<SimpleGrantedAuthority> ROLE_USER = Collections.singleton(
		new SimpleGrantedAuthority("ROLE_USER"));

	private final UserService userService;
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

	public CustomOAuth2UserService(final UserService userService) {
		this.userService = userService;
		this.delegate = new DefaultOAuth2UserService();
	}

	/* delegate 를 통한 loadUser */
	@Override
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		final OAuth2User oAuth2User = delegate.loadUser(userRequest);

		final String registrationId = userRequest.getClientRegistration().getRegistrationId();

		final SecurityOAuthType securityOAuthType = SecurityOAuthType.valueOf(registrationId.toUpperCase());
		final Map<String, Object> attributes = oAuth2User.getAttributes();

		final Email email = securityOAuthType.parseEmail(attributes);
		final OAuthType oAuthType = securityOAuthType.getOAuthType();

		userService.registerIfNotExists(email, oAuthType);
		return new DefaultOAuth2User(ROLE_USER, attributes, "email");
	}
}
