package com.meoguri.linkocean.domain.user.service.dto;

import com.meoguri.linkocean.domain.user.model.Email;
import com.meoguri.linkocean.domain.user.model.OAuthType;

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
