package com.meoguri.linkocean.util;

import static org.springframework.http.HttpHeaders.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Tokens {

	public static String get(final HttpServletRequest request) {
		return StringUtils.substringAfter(request.getHeader(AUTHORIZATION), "bearer ");
	}

	public static boolean isBlankToken(final String token) {
		return StringUtils.isBlank(token);
	}
}
