package com.meoguri.linkocean.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.Arrays;

import javax.servlet.ServletException;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.meoguri.linkocean.exception.OAuthException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomRestControllerAdvice {

	private final boolean isProd;

	public CustomRestControllerAdvice(final Environment environment) {
		this.isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException ex) {
		log.info(ex.getMessage(), ex);

		return ErrorResponse.of(BAD_REQUEST, ex.getMessage(), isProd, ex);
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({RuntimeException.class, ServletException.class})
	public ErrorResponse handleBadRequestException(final Exception ex) {
		log.info(ex.getMessage(), ex);

		return ErrorResponse.of(BAD_REQUEST, "잘못된 요청입니다.", isProd, ex);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler({Exception.class, OAuthException.class})
	public ErrorResponse handleServerException(final Exception ex) {
		log.error(ex.getMessage(), ex);

		return ErrorResponse.of(INTERNAL_SERVER_ERROR, "알 수 없는 에러가 발생했습니다. 고객센터에 문의하세요.", isProd, ex);
	}

	@Getter
	@RequiredArgsConstructor
	public static class ErrorResponse {

		private final int code;
		private final String message;
		private final String info;

		public static ErrorResponse of(
			final HttpStatus status,
			final String message,
			final boolean isProd,
			final Exception ex
		) {
			/* 운영 환경이 아니라면 예외 정보 출력 */
			final String info = isProd ? null :
								String.format("class: %s message: %s", ex.getClass().getSimpleName(), ex.getMessage());
			return new ErrorResponse(status.value(), message, info);
		}
	}
}
