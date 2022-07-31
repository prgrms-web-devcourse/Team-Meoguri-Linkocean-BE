package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.User.OAuthType;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 커스텀 OAuth2 사용자 서비스
 * - 테스트는 index.html 을 띄우고 눈으로 확인해 주세요.
 */
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final HttpSession httpSession;
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

	public CustomOAuth2UserService(final UserRepository userRepository, final HttpSession httpSession) {
		this.userRepository = userRepository;
		this.httpSession = httpSession;
		this.delegate = new DefaultOAuth2UserService();
	}

	/**
	 * delegate 를 통한 loadUser 수행 및 <br>
	 * 유저를 생성 저장하고 세션에 저장하는 역할을 추가로 수행한다.
	 */
	@Override
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		final OAuth2User oAuth2User = delegate.loadUser(userRequest);
		final String registrationId = userRequest.getClientRegistration().getRegistrationId();

		final OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());
		final User user = userOf(attributes);

		httpSession.setAttribute("user", new SessionUser(user));

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			attributes.getAttributes(),
			"email"
		);
	}

	private User userOf(final OAuthAttributes attributes) {
		return userRepository.findByEmailAndOAuthType(
				new Email(attributes.getEmail()), OAuthType.of(attributes.getOAuthType()))
			.orElseGet(() -> {
				final User user = userRepository.save(attributes.toEntity());

				log.info("새로운 사용자 저장 email : {}, oauth type : {}",
					Email.toString(user.getEmail()), user.getOAuthType());

				return user;
			});

	}
}
