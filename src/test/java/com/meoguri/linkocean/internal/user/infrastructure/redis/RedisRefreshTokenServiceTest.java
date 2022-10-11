package com.meoguri.linkocean.internal.user.infrastructure.redis;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;
import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

import io.jsonwebtoken.JwtException;

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

	@Test
	void refresh_token_갱신_성공() {
		//given
		final Long userId = 1L;
		final RegisterRefreshTokenCommand oldCommand = new RegisterRefreshTokenCommand(
			userId,
			"oldRefreshToken",
			10000L);
		refreshTokenService.registerRefreshToken(oldCommand);

		final String newRefreshToken = "newRefreshToken";
		final RegisterRefreshTokenCommand newCommand = new RegisterRefreshTokenCommand(userId, newRefreshToken, 1000L);

		//when
		refreshTokenService.registerRefreshToken(newCommand);

		//then
		final RefreshToken token = redisRefreshTokenRepository.findById(userId).get();
		assertAll(
			() -> assertThat(token).isNotNull(),
			() -> assertThat(token.getValue()).isEqualTo(newRefreshToken)
		);
	}

	@Test
	void refresh_token_검증_성공() {
		//given
		final Long userId = 1L;
		final String refreshToken = "refreshToken";
		final RegisterRefreshTokenCommand command = new RegisterRefreshTokenCommand(userId, refreshToken, 10000L);

		refreshTokenService.registerRefreshToken(command);

		//when then
		assertDoesNotThrow(() -> refreshTokenService.validateRefreshToken(userId, refreshToken));
	}

	@Test
	void refresh_token_검증_실패_옛날_refresh_token() {
		//given
		final Long userId = 1L;
		final String oldRefreshToken = "oldRefreshToken";
		final RegisterRefreshTokenCommand oldCommand = new RegisterRefreshTokenCommand(userId, oldRefreshToken, 1000L);
		refreshTokenService.registerRefreshToken(oldCommand);

		final RegisterRefreshTokenCommand newCommand = new RegisterRefreshTokenCommand(userId, "refreshToken", 1000L);
		refreshTokenService.registerRefreshToken(newCommand);

		//when then
		assertThatExceptionOfType(JwtException.class).isThrownBy(
			() -> refreshTokenService.validateRefreshToken(userId, oldRefreshToken));
	}

	@Test
	void refresh_token_삭제_성공() {
		//given
		final Long userId = 1L;
		final RegisterRefreshTokenCommand command = new RegisterRefreshTokenCommand(userId, "refreshToken", 1000L);
		refreshTokenService.registerRefreshToken(command);

		//when
		refreshTokenService.removeRefreshToken(userId);

		//then
		assertThat(redisRefreshTokenRepository.findById(userId)).isEmpty();
	}
}
