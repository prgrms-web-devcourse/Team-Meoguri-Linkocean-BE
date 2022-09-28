package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "refresh_token")
public class RefreshToken {

	@Id
	private final Long userId;
	private final String value;

	public RefreshToken(final Long userId, final String value) {
		this.userId = userId;
		this.value = value;
	}
}
