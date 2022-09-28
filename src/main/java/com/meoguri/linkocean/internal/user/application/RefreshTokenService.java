package com.meoguri.linkocean.internal.user.application;

public interface RefreshTokenService {

	/* refresh token 등록 */
	Long registerRefreshToken(Long userId, String refreshToken);
}
