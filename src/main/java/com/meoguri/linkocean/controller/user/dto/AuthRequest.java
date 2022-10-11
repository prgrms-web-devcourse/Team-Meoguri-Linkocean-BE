package com.meoguri.linkocean.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class AuthRequest {

	private String code;
	private String redirectUri;
}
