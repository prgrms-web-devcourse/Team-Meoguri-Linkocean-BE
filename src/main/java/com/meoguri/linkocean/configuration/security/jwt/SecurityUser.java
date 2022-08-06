package com.meoguri.linkocean.configuration.security.jwt;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.Getter;

@Getter
public final class SecurityUser extends User {

	private final long id;
	private final Optional<Long> profileId; // profileId is nullable

	public SecurityUser(
		final long id,
		final Long profileId,
		final String email,
		final String oAuthType,
		final Collection<? extends GrantedAuthority> authorities
	) {

		super(email, oAuthType, authorities);
		this.id = id;
		this.profileId = Optional.ofNullable(profileId);
	}

	public long getProfileId() {
		return profileId.orElseThrow(LinkoceanRuntimeException::new);
	}
}
