package com.meoguri.linkocean.internal.user.application.dto;

import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUserCommand {
	private final OAuthType oAuthType;
	private final String authorizationCode;
	private final String redirectUri;
}
