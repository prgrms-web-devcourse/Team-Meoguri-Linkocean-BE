package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.internal.user.application.RefreshTokenService;
import com.meoguri.linkocean.internal.user.application.dto.RegisterRefreshTokenCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisRefreshTokenService implements RefreshTokenService {

	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	@Override
	public Long registerRefreshToken(final RegisterRefreshTokenCommand command) {
		final RefreshToken token = new RefreshToken(
			command.getUserId(),
			command.getRefreshToken(),
			command.getExpiration());

		redisRefreshTokenRepository.save(token);

		return token.getUserId();
	}
}
