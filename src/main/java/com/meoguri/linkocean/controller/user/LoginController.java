package com.meoguri.linkocean.controller.user;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.user.dto.LoginSuccessReponse;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

	private final ProfileService profileService;
	private final UserService userService;

	/**
	 * OAuthLogin의 Default Success Url 로 등록된다
	 *  로그인이 성공하면 사용자가 정상적인
	 *  회원가입 절차를 통해 프로필이 등록 된 사용자인지 알려준다e
	 */
	// @GetMapping("/success")
	// public LoginSuccessReponse loginSuccess(@LoginUser SessionUser sessionUser) {
	// 	return LoginSuccessReponse.of();
	// }
	@PostMapping
	public LoginSuccessReponse saveAndHasProfile(@RequestBody Map<String, String> paramMap) {
		final long savedId = userService.saveOrUpdate(paramMap.get("email"), paramMap.get("oauthType"));
		return LoginSuccessReponse.of(profileService.existsByUserId(savedId));

	}

}
