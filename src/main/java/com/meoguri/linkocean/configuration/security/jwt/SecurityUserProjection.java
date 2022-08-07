package com.meoguri.linkocean.configuration.security.jwt;

/* Security User 조회 시 profile id 까지 조회 하기 위한 Projection */
public interface SecurityUserProjection {

	long getId();

	String getEmail();

	String getOauthType();

	Long getProfile_id();
}
