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

		/**
		 * 권한 (authority, role)에 대한 구현이 없으므로 emptySet 전달 -> ROLE 없이 인증으로 처리
		 * name 을 scope 에 포함 하지 않았기 때문에 email 을 그냥 username 으로 처리 하기 위해
		 * nameAttributeKey 에 email 전달
		 */
		return new DefaultOAuth2User(
			Collections.emptySet(),
			attributes.getAttributes(),
			"email"
		);
	}

	/**
	 * 여러 Vendor사에 동일한 이메일로 등록한 유저가 있을 수 있음
	 * ex) Google, Kakao에 동일한 이메일로 가입되어 있음
	 * User의 이메일은 Unique해야 하기 때문에 Google 혹은 Kakao 둘 중 하나만 가입 가능하도록 함
	 */
	private User userOf(final OAuthAttributes attributes) {

		final User findUser = userRepository.findByEmail(new Email(attributes.getEmail())).orElseGet(() -> {
			final User user = attributes.toEntity();
			log.info("새로운 사용자 저장 email : {}, oauth type : {}", Email.toString(user.getEmail()), user.getOAuthType());
			return userRepository.save(user);
		});

		// 기존에 회원가입 했던 Vendor사가 아닌 다른 Vendor 사에서 요청이 들어온다면 막기
		if (findUser.getOAuthType() != attributes.getOAuthType()) {
			throw new IllegalArgumentException("사용자는 하나의 Vendor 사에서 회원가입이 가능합니다.");
		}

		return findUser;
	}
}
