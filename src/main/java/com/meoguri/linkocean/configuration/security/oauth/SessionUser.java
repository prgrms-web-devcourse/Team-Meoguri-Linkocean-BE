package com.meoguri.linkocean.configuration.security.oauth;

import java.io.Serializable;

import com.meoguri.linkocean.domain.user.entity.User;

import lombok.Getter;

/**
 * 세선에 저장할 사용자 정보
 * - 일단 최소한의 정보인 id 만 포함
 */
@Getter
public class SessionUser implements Serializable {

	private final long id;

	public SessionUser(final User user) {
		this.id = user.getId();
	}
}
