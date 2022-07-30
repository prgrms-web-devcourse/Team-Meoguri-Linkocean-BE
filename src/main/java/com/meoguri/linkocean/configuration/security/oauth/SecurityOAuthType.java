package com.meoguri.linkocean.configuration.security.oauth;

import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

/**
 * 지원하는 OAuth 벤더사의 목록 <br>
 * open api 를 통해 조회한 객체를 커스텀하게 정의한 OAuthAttribute 로 옮겨 담는 로직을 포함한다 <br>
 * <br>
 * 참고 <br>
 * <li> 구글   Scope : auth/userinfo.email - <a href ="https://developers.google.com/identity/protocols/oauth2/openid-connect#obtainuserinfo">oauth2/openid-connect#obtainuserinfo</a></li>
 * <li> 네이버 Scope : email - <a href="https://developers.naver.com/docs/login/profile/profile.md">네이버 회원 프로필 조회 API 명세</a></li>
 * <li> 카카오 Scope : account_email- <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount">카카오 로그인>RESTAPI#KakaoAccount</a></li>
 */
@RequiredArgsConstructor
public enum SecurityOAuthType {

	GOOGLE(attributes ->
		new OAuthAttributes(
			attributes,
			(String)attributes.get("email"),
			"GOOGLE"
		)
	),

	NAVER(attributes -> {
		@SuppressWarnings("unchecked") // Api Spec 상 항상 Map 이 보장된다.
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return new OAuthAttributes(
			response,
			(String)response.get("email"),
			"NAVER"
		);
	}),

	KAKAO(attributes -> {
		@SuppressWarnings("unchecked") // Api Spec 상 항상 Map 이 보장된다.
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

		return new OAuthAttributes(
			kakaoAccount,
			(String)kakaoAccount.get("email"),
			"KAKAO"
		);
	});

	public final Function<Map<String, Object>, OAuthAttributes> toOAuthAttributes;

	public static SecurityOAuthType of(final String type) {
		try {
			return SecurityOAuthType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException();
		}
	}

}
