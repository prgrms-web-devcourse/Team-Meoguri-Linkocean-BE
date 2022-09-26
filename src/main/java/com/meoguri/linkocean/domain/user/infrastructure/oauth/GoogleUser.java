package com.meoguri.linkocean.domain.user.infrastructure.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleUser {
	public String id;
	public String email;
	public Boolean verifiedEmail;
	public String picture;
}
