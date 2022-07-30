package com.meoguri.linkocean.controller.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginSuccessResponse {

	private final Long id;
	private final boolean hasProfile;

}
