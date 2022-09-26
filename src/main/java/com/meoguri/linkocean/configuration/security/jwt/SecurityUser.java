package com.meoguri.linkocean.configuration.security.jwt;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.meoguri.linkocean.domain.user.model.Email;
import com.meoguri.linkocean.domain.user.model.OAuthType;

import lombok.Getter;

@Getter
public final class SecurityUser extends User {

	private final long id;
	private final Long profileId;

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
		/* 회원 가입만 하고 프로필 등록을 하지 않은 경우 프로필 id 는 null 이다 */
		checkCondition(profileId != null, "profile is null");
		return profileId;
	}

	public boolean hasProfile() {
		return profileId != null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", id)
			.append("profileId", profileId)
			.toString();
	}
}
