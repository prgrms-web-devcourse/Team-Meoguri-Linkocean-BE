package com.meoguri.linkocean.internal.user.application;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.internal.user.application.dto.GetAuthTokenResult;
import com.meoguri.linkocean.internal.user.domain.UserService;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuthAuthenticationService {

	private final OAuthClient oAuthClient;
	private final JwtProvider jwtProvider;

	private final UserService userService;

	/* oAuthType에 맞는 소셜 로그인 uri를 반환한다.- 테스트용 */
	@Deprecated
	public String getAuthorizationUri(final OAuthType oAuthType) {

		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("구글 소셜 로그인만 지원되는 기능입니다.");
		}

		return oAuthClient.getAuthorizationUri();
	}

	/**
	 * 사용자 인증하고 Jwt 토큰 발급
	 * 1. 인증 코드 이용해 third party resource server에 접근할 수 있는 access token 받기
	 * 2. access token 이용해 third party resource server에서 사용자 이메일 정보 받기
	 * 3. DB에 사용자 정보 없으면 저장하기
	 * 4. Jwt 토큰 발급 후 반환
	 */
	public GetAuthTokenResult authenticate(final OAuthType oAuthType, final String authorizationCode,
		final String redirectUri) {

		//TODO 벤더사 추가 쉽도록 확장성 있게 리팩토링하기, 아직 구글만 지원함.
		if (oAuthType != OAuthType.GOOGLE) {
			throw new IllegalArgumentException("구글 소셜 로그인만 지원되는 기능입니다.");
		}

		final String accessToken = oAuthClient.getAccessToken(authorizationCode, redirectUri);
		final Email email = oAuthClient.getUserEmail(accessToken);

		final long userId = userService.registerIfNotExists(email, oAuthType);

		//TODO: refresh token, redis 도입하기
		return new GetAuthTokenResult(
			jwtProvider.generateAccessToken(email, oAuthType),
			jwtProvider.generateRefreshToken(userId));
	}
}
