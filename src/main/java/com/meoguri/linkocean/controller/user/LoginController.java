package com.meoguri.linkocean.controller.user;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.domain.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

	private final ProfileService profileService;

	/**
	 * OAuthLogin의 Default Success Url 로 등록된다
	 *  로그인이 성공하면 사용자가 정상적인
	 *  회원가입 절차를 통해 프로필이 등록 된 사용자인지 알려준다
	 */
	@GetMapping("/success")
	public Map<String, Boolean> loginSuccess(@LoginUser SessionUser sessionUser) {
		return Map.of("hasProfile", profileService.existsByUserId(sessionUser.getId()));
	}

}
