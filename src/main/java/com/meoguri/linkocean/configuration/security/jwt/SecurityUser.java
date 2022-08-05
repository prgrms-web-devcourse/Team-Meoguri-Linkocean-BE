package com.meoguri.linkocean.configuration.security.jwt;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class SecurityUser extends User {

	private final Long id;
	private final String email;
	private final String oAuthType;

	public SecurityUser(
		final Long id,
		final String email,
		final String oAuthType,
		final Collection<? extends GrantedAuthority> authorities) {

		super(email, oAuthType, authorities);
		this.id = id;
		this.email = email;
		this.oAuthType = oAuthType;
	}

	public static <R> R defaultIfNull(final SecurityUser securityUser,
		final Function<SecurityUser, R> securityUserResolver, final R defaultValue) {
		return Optional.ofNullable(securityUser)
			.map(securityUserResolver)
			.orElse(defaultValue);
	}

}
