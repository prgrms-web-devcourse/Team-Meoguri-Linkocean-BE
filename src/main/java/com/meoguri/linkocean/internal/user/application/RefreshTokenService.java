package com.meoguri.linkocean.internal.user.application;

import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

public interface RefreshTokenService {

	/* refresh token 등록 */
	Long registerRefreshToken(RegisterRefreshTokenCommand command);
}
