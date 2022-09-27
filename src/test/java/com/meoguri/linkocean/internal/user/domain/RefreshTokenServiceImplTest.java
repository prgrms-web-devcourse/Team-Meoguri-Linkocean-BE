package com.meoguri.linkocean.internal.user.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.test.support.internal.service.BaseServiceTest;

class RefreshTokenServiceImplTest extends BaseServiceTest {

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Test
	void refresh_token_저장_등록_성공() {
		//given
		final Long userId = 1L;
		final String refreshToken = "refreshToken";

		//when
		final Long registeredId = refreshTokenService.registerRefreshToken(userId, refreshToken);

		//then
		assertThat(registeredId).isEqualTo(userId);
	}
}