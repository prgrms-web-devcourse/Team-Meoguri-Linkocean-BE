package com.meoguri.linkocean.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.user.dto.LoginSuccessResponse;
import com.meoguri.linkocean.domain.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

	private final ProfileService profileService;

	@GetMapping("/login/success")
	public LoginSuccessResponse loginSuccess(@LoginUser SessionUser sessionUser) {

		final boolean hasProfile = profileService.existsByUserId(sessionUser.getId());

		return new LoginSuccessResponse(sessionUser.getId(), hasProfile);
	}

}
