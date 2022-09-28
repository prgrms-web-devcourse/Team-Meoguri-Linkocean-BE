package com.meoguri.linkocean.internal.user.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RegisterRefreshTokenCommand {
	private final long userId;
	private final String refreshToken;
	private final long expiration;
}
