package com.meoguri.linkocean.internal.user.domain;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.internal.user.domain.model.RefreshToken;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public Long registerRefreshToken(final Long userId, final String refreshToken) {
		final RefreshToken token = new RefreshToken(userId, refreshToken);
		refreshTokenRepository.save(token);

		return token.getUserId();
	}
}
