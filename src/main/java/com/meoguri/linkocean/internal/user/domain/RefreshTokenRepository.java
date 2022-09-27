package com.meoguri.linkocean.internal.user.domain;

import org.springframework.data.repository.CrudRepository;

import com.meoguri.linkocean.internal.user.domain.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
