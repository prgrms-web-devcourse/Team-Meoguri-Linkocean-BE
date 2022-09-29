package com.meoguri.linkocean.controller.user;

import static org.springframework.http.HttpStatus.*;

import java.net.URI;

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
import com.meoguri.linkocean.controller.user.dto.AuthResponse;
import com.meoguri.linkocean.internal.user.application.OAuthAuthenticationService;
import com.meoguri.linkocean.internal.user.application.dto.AuthUserCommand;
import com.meoguri.linkocean.internal.user.application.dto.GetAuthTokenResult;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

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
		@PathVariable("oAuthType") OAuthType oAuthType
	) {
		final String authorizationUri = oAuthAuthenticationService.getAuthorizationUri();

		final HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(authorizationUri));
		return new ResponseEntity<>(headers, PERMANENT_REDIRECT);
	}

	/* 테스트용 API - 프론트 역할 */
	@Deprecated
	@GetMapping("/{oAuthType}")
	public AuthResponse authenticate(
		@PathVariable("oAuthType") OAuthType oAuthType,
		@RequestParam("code") String code
	) {
		final AuthRequest request = new AuthRequest(code, "https://localhost/api/v1/auth/google");
		return authenticate(oAuthType, request);
	}

	@PostMapping("/{oAuthType}")
	public AuthResponse authenticate(
		@PathVariable("oAuthType") OAuthType oAuthType,
		@RequestBody AuthRequest authRequest
	) {
		final GetAuthTokenResult getAuthTokenResult = oAuthAuthenticationService.authenticate(new AuthUserCommand(
			oAuthType,
			authRequest.getCode(),
			authRequest.getRedirectUri()));

		return new AuthResponse(
			getAuthTokenResult.getAccessToken(),
			getAuthTokenResult.getRefreshToken(),
			"Bearer");
	}
}
