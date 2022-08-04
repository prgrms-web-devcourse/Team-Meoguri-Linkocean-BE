package com.meoguri.linkocean.controller.user;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.user.dto.LoginSuccessReponse;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.user.service.UserService;

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
	@GetMapping("/success")
	public LoginSuccessReponse loginSuccess(@AuthenticationPrincipal SecurityUser user) {
		return LoginSuccessReponse.of(profileService.existsByUserId(user.id()));
	}

	@PostMapping
	public ResponseEntity<Void> saveAndHasProfile(@RequestBody Map<String, String> paramMap) {
		final String token = userService.saveOrUpdate(paramMap.get("email"), paramMap.get("oauthType"));
		final ResponseCookie tokenCookie = generateLoginSuccessCookie(token);
		return ResponseEntity
			.noContent()
			.header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
			.build();
	}

	private ResponseCookie generateLoginSuccessCookie(final String accessToken) {
		return ResponseCookie.from("access-token", accessToken)
			.httpOnly(true)
			.path("/")
			.build();
	}

}
