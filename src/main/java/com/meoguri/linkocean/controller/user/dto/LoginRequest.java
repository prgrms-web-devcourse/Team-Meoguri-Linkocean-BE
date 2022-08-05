package com.meoguri.linkocean.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class LoginRequest {

	private String email;
	private String oauthType;
}
