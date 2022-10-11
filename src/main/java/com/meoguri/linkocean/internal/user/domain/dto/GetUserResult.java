package com.meoguri.linkocean.internal.user.domain.dto;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetUserResult {

	private final long id;
	private final Long profileId;
	private final Email email;
	private final OAuthType oauthType;
}
