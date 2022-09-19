package com.meoguri.linkocean.domain.user.service;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuthAuthenticationService implements AuthenticationService {

	private final OAuthClient oAuthClient;
	private final JwtProvider jwtProvider;

	private final UserService userService;

	@Override
	public String getAuthorizationUri(final OAuthType oAuthType) {

		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("구글 소셜 로그인만 지원되는 기능입니다.");
		}

		return oAuthClient.getAuthorizationUri();
	}

	@Override
	public String authenticate(final OAuthType oAuthType, final String authorizationCode) {

		//TODO 벤더사 추가 쉽도록 확장성 있게 리팩토링하기, 아직 구글만 지원함.
		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("구글 소셜 로그인만 지원되는 기능입니다.");
		}

		final String accessToken = oAuthClient.getAccessToken(authorizationCode);
		final Email email = oAuthClient.getUserEmail(accessToken);

		userService.registerIfNotExists(email, oAuthType);

		//TODO: refresh token, redis 도입하기
		return jwtProvider.generate(email, oAuthType);
	}
}
