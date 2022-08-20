package com.meoguri.linkocean.configuration.security.jwt;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

import lombok.Getter;

@Getter
public final class SecurityUser extends User {

	private final long id;
	private final Long profileId; // profileId is nullable

	public SecurityUser(
		final long id,
		final Long profileId,
		final Email email,
		final OAuthType oAuthType,
		final Collection<? extends GrantedAuthority> authorities
	) {

		super(Email.toString(email), oAuthType.name(), authorities);
		this.id = id;
		this.profileId = profileId;
	}

	public long getProfileId() {
		checkCondition(profileId != null, "profile is null");
		return profileId;
	}

	public boolean hasProfile() {
		return profileId != null;
	}

}
