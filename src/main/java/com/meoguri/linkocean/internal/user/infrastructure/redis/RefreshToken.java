package com.meoguri.linkocean.internal.user.infrastructure.redis;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.concurrent.TimeUnit.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "refresh_token")
public class RefreshToken {

	@Id
	private final Long userId;

	private final String value;

	@TimeToLive(unit = MILLISECONDS)
	private final long expiration;

	public RefreshToken(final Long userId, final String value, final long expiration) {
		checkNotNull(userId);
		checkNotNull(value);
		checkArgument(expiration > 0, "만료 기간은 0보다 작을 수 없습니다.");

		this.userId = userId;
		this.value = value;
		this.expiration = expiration;
	}
}
