package com.meoguri.linkocean.internal.user.application;

import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

public interface RefreshTokenService {

	/* refresh token 등록 */
	void registerRefreshToken(RegisterRefreshTokenCommand command);

	/* 최신 refresh token인지 검증 */
	void validateRefreshToken(Long userId, String refreshToken);

	/* refresh token 제거 */
	void removeRefreshToken(Long userId);
}
