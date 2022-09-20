package com.meoguri.linkocean.controller.user;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.service.OAuthAuthenticationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

	private final OAuthAuthenticationService oAuthAuthenticationService;

	//TODO: 프론트랑 통합하면 해당 API는 없어질 예정
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

	//TODO: 프론트랑 통합하면 HTTP METHOD GET 제거하기
	@RequestMapping(value = "/{oAuthType}", method = {GET, POST})
	public Map<String, Object> authenticate(
		@PathVariable("oAuthType") String oAuthType,
		@RequestParam("code") String code
	) {
		final String jwt = oAuthAuthenticationService.authenticate(OAuthType.of(oAuthType.toUpperCase()), code);

		return Map.of("token", jwt);
	}
}
