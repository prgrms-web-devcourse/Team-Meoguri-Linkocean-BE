package com.meoguri.linkocean.controller.user;

import static org.springframework.http.HttpStatus.*;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.user.dto.AuthRequest;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.OAuthAuthenticationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

	private final OAuthAuthenticationService oAuthAuthenticationService;

	/* 테스트용 API - 프론트 역할 */
	@Deprecated
	@GetMapping("/{oAuthType}/temp")
	public ResponseEntity<Void> redirectToAuthorizationUri(
		@PathVariable("oAuthType") String oAuthType
	) {
		final String authorizationUri = oAuthAuthenticationService.getAuthorizationUri(
			OAuthType.of(oAuthType.toUpperCase()));

		final HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(authorizationUri));
		return new ResponseEntity<>(headers, PERMANENT_REDIRECT);
	}

	/* 테스트용 API - 프론트 역할 */
	@Deprecated
	@GetMapping("/{oAuthType}")
	public Map<String, Object> authenticate(
		@PathVariable("oAuthType") String oAuthType,
		@RequestParam("code") String code
	) {
		final AuthRequest request = new AuthRequest(code, "https://localhost/api/v1/auth/google");
		return authenticate(oAuthType, request);
	}

	@PostMapping("/{oAuthType}")
	public Map<String, Object> authenticate(
		@PathVariable("oAuthType") String oAuthType,
		@RequestBody AuthRequest authRequest
	) {
		final String jwt = oAuthAuthenticationService.authenticate(
			OAuthType.of(oAuthType.toUpperCase()),
			authRequest.getCode(),
			authRequest.getRedirectUri());

		return Map.of("token", jwt);
	}
}
