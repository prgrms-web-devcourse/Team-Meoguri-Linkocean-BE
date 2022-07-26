package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final HttpSession httpSession;

	@Override
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		final OAuth2User oAuth2User = delegate.loadUser(userRequest);

		final String registrationId = userRequest.getClientRegistration().getRegistrationId();

		final OAuthAttributes attributes
			= OAuthAttributes.of(registrationId, oAuth2User.getAttributes());

		final User user = save(attributes);

		httpSession.setAttribute("user", new SessionUser(user));

		return new DefaultOAuth2User(
			// 권한 (authority, role)에 대한 구현이 없으므로 emptySet 전달
			Collections.emptySet(),
			attributes.getAttributes(),

			// name 을 scope 에 포함 하지 않았기 때문에 email 을 그냥 username 으로 처리 하기 위해
			// nameAttributeKey 에 email 전달
			"email"
		);
	}

	private User save(final OAuthAttributes attributes) {
		final User user = userRepository.findByEmail(new Email(attributes.getEmail()))
			.orElse(attributes.toEntity());

		log.info("새로운 사용자 저장 email : {}, oauth type : {}", Email.toString(user.getEmail()), user.getOAuthType());
		return userRepository.save(user);
	}
}
