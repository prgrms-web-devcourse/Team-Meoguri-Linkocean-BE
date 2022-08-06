package com.meoguri.linkocean.util;

import static lombok.AccessLevel.*;
import static org.springframework.http.HttpHeaders.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class TokenUtil {

	public static String get(final HttpServletRequest request) {
		return StringUtils.substringAfter(request.getHeader(AUTHORIZATION), "Bearer ");
	}

	public static boolean isBlankToken(final String token) {
		return StringUtils.isBlank(token);
	}
}
