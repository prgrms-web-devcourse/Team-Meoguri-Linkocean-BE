package com.meoguri.linkocean.internal.user.infrastructure.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;

@DataRedisTest
@Import(RedisRefreshTokenService.class)
class RedisRefreshTokenServiceTest {

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private RedisRefreshTokenRepository redisRefreshTokenRepository;

	@AfterEach
	void cleanup() {
		redisRefreshTokenRepository.deleteAll();
	}

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
