package com.meoguri.linkocean.internal.user.infrastructure.redis;

import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
