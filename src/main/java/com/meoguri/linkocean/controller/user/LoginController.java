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
@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

	private final ProfileService profileService;

	/**
	 * OAuthLogin의 Default Success Url 로 등록된다
	 */
	@GetMapping("/success")
	public LoginSuccessResponse loginSuccess(@LoginUser SessionUser sessionUser) {
		return new LoginSuccessResponse(profileService.existsByUserId(sessionUser.getId()));
	}

}
