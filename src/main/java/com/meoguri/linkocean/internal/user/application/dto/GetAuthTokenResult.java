package com.meoguri.linkocean.internal.user.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetAuthTokenResult {
	private final String accessToken;
	private final String refreshToken;
}
