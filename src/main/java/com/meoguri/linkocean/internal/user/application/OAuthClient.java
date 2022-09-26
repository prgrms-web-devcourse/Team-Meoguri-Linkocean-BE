package com.meoguri.linkocean.internal.user.application;

import com.meoguri.linkocean.internal.user.domain.model.Email;

public interface OAuthClient {

	/* 프론트에서 가져갈 일 */
	@Deprecated
	String getAuthorizationUri();

	/* 인증 코드 이용해 access Token 얻어온다 */
	String getAccessToken(String authorizationCode, String redirectUri);

	/* access token 이용해 사용자 이메일 정보를 얻어온다 */
	Email getUserEmail(String accessToken);
}
