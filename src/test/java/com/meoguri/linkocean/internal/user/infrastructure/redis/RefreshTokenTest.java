package com.meoguri.linkocean.internal.user.infrastructure.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RefreshTokenTest {

	@Test
	void 생성_성공() {
		//given
		final Long userId = 1L;
		final String token = "valid refresh token";
		final long expiration = 1000L;

		//when
		final RefreshToken refreshToken = new RefreshToken(userId, token, expiration);

		//then
		assertThat(refreshToken).isNotNull();
	}

	@Test
	void 생성_실패_사용자_아이디_없음() {
		//given
		final Long nullUserId = null;

		//when then
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> new RefreshToken(nullUserId, "refresh token", 1000L));
	}

	@Test
	void 생성_실패_토큰_값_없음() {
		//given
		final String nullToken = null;

		//when then
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> new RefreshToken(1L, nullToken, 1000L));
	}

	@Test
	void 생성_실패_유효하지_않은_만료기간() {
		//given
		final long invalidExpiration = -1L;

		//when then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new RefreshToken(1L, "refresh token", invalidExpiration));
	}
}
