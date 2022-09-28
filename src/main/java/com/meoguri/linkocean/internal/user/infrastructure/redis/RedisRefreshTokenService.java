package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisRefreshTokenService implements RefreshTokenService {

	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	@Override
	public Long registerRefreshToken(final Long userId, final String refreshToken) {
		final RefreshToken token = new RefreshToken(userId, refreshToken);
		redisRefreshTokenRepository.save(token);

		return token.getUserId();
	}
}
