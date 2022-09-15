package com.meoguri.linkocean.infrastructure.oauth.google;

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
