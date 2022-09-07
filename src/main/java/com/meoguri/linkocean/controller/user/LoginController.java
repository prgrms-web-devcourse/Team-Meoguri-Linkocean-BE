package com.meoguri.linkocean.controller.user;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.user.dto.LoginRequest;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

	private final UserService userService;

	private final JwtProvider jwtProvider;

	/* 로그인 - 토큰을 반환한다 */
	@PostMapping
	public Map<String, Object> login(
		@RequestBody LoginRequest request
	) {
		final Email email = new Email(request.getEmail());
		final OAuthType oAuthType = OAuthType.of(request.getOauthType());

		userService.registerIfNotExists(email, oAuthType);
		return Map.of("token", jwtProvider.generate(email, oAuthType));
	}

	/**
	 * OAuthLogin 의 Default Success Url 로 등록된다
	 *  로그인이 성공하면 사용자가 회원가입 절차를 완료하여
	 *  프로필을 등록 한 사용자인지 알려준다
	 */
	@GetMapping("/success")
	public Map<String, Object> loginSuccess(
		@AuthenticationPrincipal SecurityUser user
	) {
		return Map.of("hasProfile", user.hasProfile());
	}

}
