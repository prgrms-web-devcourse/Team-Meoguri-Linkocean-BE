package com.meoguri.linkocean.infrastructure.oauth.google;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperties {

	private String clientId;
	private String clientSecret;
	private String scope;
	private String grantType;
	private String responseType;

	private String authorizationUri;
	private String tokenUri;
	private String userInfoUri;
}
