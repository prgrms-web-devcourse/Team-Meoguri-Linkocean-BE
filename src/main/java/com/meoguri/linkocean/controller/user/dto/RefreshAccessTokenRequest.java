package com.meoguri.linkocean.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class RefreshAccessTokenRequest {

	private String refreshToken;
	private String tokenType;
}
