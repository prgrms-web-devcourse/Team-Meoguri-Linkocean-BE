package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;
import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisRefreshTokenService implements RefreshTokenService {

	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	@Override
	public void registerRefreshToken(final RegisterRefreshTokenCommand command) {
		final RefreshToken token = new RefreshToken(
			command.getUserId(),
			command.getRefreshToken(),
			command.getExpiration());

		redisRefreshTokenRepository.save(token);
	}

	@Override
	public void validateRefreshToken(final Long userId, final String refreshToken) {
		final RefreshToken token = redisRefreshTokenRepository.findById(userId)
			.orElseThrow(() -> new JwtException("존재하지 않는 refresh token 입니다."));

		token.isSameRefreshToken(refreshToken);
	}

	@Override
	public void removeRefreshToken(final Long userId) {
		redisRefreshTokenRepository.deleteById(userId);
	}
}
