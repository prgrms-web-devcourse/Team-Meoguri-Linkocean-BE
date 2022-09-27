package com.meoguri.linkocean.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import com.meoguri.linkocean.internal.user.infrastructure.redis.RedisProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedisConfiguration {

	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}
}
