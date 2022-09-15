package com.meoguri.linkocean.controller.user;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

	private final AuthenticationService authenticationService;
	private final JwtProvider jwtProvider;

	//TODO: 프론트랑 통합하면 해당 API는 없어질 예정
	@Deprecated
	@GetMapping("/{oAuthType}/temp")
	public void socialLoginRedirect(
		@PathVariable("oAuthType") String oAuthType,
		HttpServletResponse response) throws IOException {

		final OAuthType type = OAuthType.of(oAuthType.toUpperCase());
		final String redirectUrl = authenticationService.getRedirectUrl(type);

		response.sendRedirect(redirectUrl);
	}

	//TODO: 프론트랑 통합하면 HTTP METHOD POST로 변경하기
	@GetMapping("/{oAuthType}")
	public Map<String, Object> authenticate(
		@PathVariable("oAuthType") String oAuthType,
		@RequestParam("code") String code
	) throws JsonProcessingException {
		OAuthType type = OAuthType.of(oAuthType.toUpperCase());
		final Email email = authenticationService.authenticate(type, code);

		//TODO: refresh token, redis 도입하기
		return Map.of("token", jwtProvider.generate(email, type));
	}
}
