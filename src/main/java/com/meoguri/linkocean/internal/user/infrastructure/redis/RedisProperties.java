package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

	private String host;
	private int port;
}
