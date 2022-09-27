package com.meoguri.linkocean.controller.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse {

	private final String accessToken;
	private final String refreshToken;
	private final String tokenType;
}
