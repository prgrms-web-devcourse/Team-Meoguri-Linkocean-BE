package com.meoguri.linkocean.internal.user.application;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.internal.user.application.dto.AuthUserCommand;
import com.meoguri.linkocean.internal.user.application.dto.GetAuthTokenResult;
import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;
import com.meoguri.linkocean.internal.user.domain.UserService;
import com.meoguri.linkocean.internal.user.domain.model.Email;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuthAuthenticationService {

	private final OAuthClient oAuthClient;
	private final JwtProvider jwtProvider;

	private final UserService userService;
	private final RefreshTokenService refreshTokenService;

	/* oAuthType에 맞는 소셜 로그인 uri를 반환한다.- 테스트용 */
	@Deprecated
	public String getAuthorizationUri() {
		return oAuthClient.getAuthorizationUri();
	}

	/**
	 * 사용자 인증하고 Jwt 토큰 발급
	 * 1. 인증 코드 이용해 third party resource server에 접근할 수 있는 access token 받기
	 * 2. access token 이용해 third party resource server에서 사용자 이메일 정보 받기
	 * 3. DB에 사용자 정보 없으면 저장하기
	 * 4. Jwt 토큰 발급 후 반환
	 */
	public GetAuthTokenResult authenticate(final AuthUserCommand command) {

		final String accessToken = oAuthClient.getAccessToken(command.getAuthorizationCode(), command.getRedirectUri());
		final Email email = oAuthClient.getUserEmail(accessToken);

		final long userId = userService.registerIfNotExists(email, command.getOAuthType());

		final String linkoceanAccessToken = jwtProvider.generateAccessToken(email, command.getOAuthType());
		final String linkoceanRefreshToken = jwtProvider.generateRefreshToken(userId);

		refreshTokenService.registerRefreshToken(new RegisterRefreshTokenCommand(
			userId,
			linkoceanRefreshToken,
			jwtProvider.getRefreshTokenExpiration()
		));

		return new GetAuthTokenResult(linkoceanAccessToken, linkoceanRefreshToken);
	}
}
