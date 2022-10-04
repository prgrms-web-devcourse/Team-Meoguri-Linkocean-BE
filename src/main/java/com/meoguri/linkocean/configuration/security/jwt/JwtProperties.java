package com.meoguri.linkocean.configuration.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secretKey;
	private long accessTokenExpiration;
	private long refreshTokenExpiration;
}
