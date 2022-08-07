package com.meoguri.linkocean.configuration.security.jwt;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public final class SecurityUser extends User {

	private final long id;
	private final Long profileId; // profileId is nullable

	public SecurityUser(
		final long id,
		final Long profileId,
		final String email,
		final String oAuthType,
		final Collection<? extends GrantedAuthority> authorities
	) {

		super(email, oAuthType, authorities);
		this.id = id;
		this.profileId = profileId;
	}

	public long getProfileId() {
		checkCondition(profileId != null);
		return profileId;
	}
}
