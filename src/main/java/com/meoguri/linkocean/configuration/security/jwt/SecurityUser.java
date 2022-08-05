package com.meoguri.linkocean.configuration.security.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public final class SecurityUser extends User {

	private final Long id;

	public SecurityUser(
		final Long id,
		final String email,
		final String oAuthType,
		final Collection<? extends GrantedAuthority> authorities
	) {

		super(email, oAuthType, authorities);
		this.id = id;
	}
}
