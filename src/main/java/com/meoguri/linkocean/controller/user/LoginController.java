package com.meoguri.linkocean.controller.user;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.user.dto.LoginRequest;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

	private final UserService userService;
	private final ProfileService profileService;

	/* 로그인 - 토큰을 반환한다 */
	@PostMapping
	public Map<String, Object> login(
		@RequestBody LoginRequest request
	) {
		final String result = userService.getOrSaveAndRetrieveToken(
			new Email(request.getEmail()),
			OAuthType.of(request.getOauthType())
		);

		return Map.of("token", result);
	}

	/**
	 * OAuthLogin 의 Default Success Url 로 등록된다
	 *  로그인이 성공하면 사용자가 정상적인
	 *  회원가입 절차를 통해 프로필이 등록 된 사용자인지 알려준다
	 */
	@GetMapping("/success")
	public Map<String, Object> loginSuccess(
		@AuthenticationPrincipal SecurityUser user
	) {
		return Map.of("hasProfile", profileService.existsByUserId(user.getId()));
	}

}
