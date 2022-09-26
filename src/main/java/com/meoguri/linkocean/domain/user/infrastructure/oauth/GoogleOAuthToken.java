package com.meoguri.linkocean.domain.user.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleOAuthToken {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private int expiresIn;

	private String scope;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("id_token")
	private String idToken;
}
