package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public interface AuthenticationService {

	String getAuthorizationUri(OAuthType oAuthType);
	
	String authenticate(OAuthType oAuthType, String authorizationCode);
}
