package com.meoguri.linkocean.controller.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginSuccessReponse {

	private final boolean hasProfile;

	public static LoginSuccessReponse of(final boolean hasProfile) {
		return new LoginSuccessReponse(hasProfile);
	}
}
