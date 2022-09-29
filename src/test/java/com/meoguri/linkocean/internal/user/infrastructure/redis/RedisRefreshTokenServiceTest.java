package com.meoguri.linkocean.internal.user.infrastructure.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;
import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

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
		final RegisterRefreshTokenCommand command = new RegisterRefreshTokenCommand(1L, "refreshToken", 10000L);

		//when
		refreshTokenService.registerRefreshToken(command);

		//then
		assertThat(redisRefreshTokenRepository.findById(userId)).isPresent();
	}
}
