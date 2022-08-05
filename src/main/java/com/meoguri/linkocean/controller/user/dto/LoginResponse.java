package com.meoguri.linkocean.controller.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {

	private final String token;

	public static LoginResponse of(final String token) {
		return new LoginResponse(token);
	}

}
