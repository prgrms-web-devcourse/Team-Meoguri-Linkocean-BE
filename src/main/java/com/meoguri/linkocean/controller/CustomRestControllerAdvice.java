package com.meoguri.linkocean.controller;

import static org.springframework.http.HttpStatus.*;

import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomRestControllerAdvice {

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn(ex.getMessage(), ex);

		return ErrorResponse.of(BAD_REQUEST, ex.getMessage(), ex);
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({RuntimeException.class, ServletException.class})
	public ErrorResponse handleBadRequestException(Exception ex) {
		log.warn(ex.getMessage(), ex);

		return ErrorResponse.of(BAD_REQUEST, "잘못된 요청입니다.", ex);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleServerException(Exception ex) {
		log.warn(ex.getMessage(), ex);

		return ErrorResponse.of(INTERNAL_SERVER_ERROR, "알 수 없는 에러가 발생했습니다. 고객센터에 문의하세요.", ex);
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorResponse {

		private int code;
		private String message;
		private String info; // 정신 건강을 위해 추가

		public static ErrorResponse of(HttpStatus status, String message, Exception ex) {
			final String info = "class : " + ex.getClass().getSimpleName() + " message : " + ex.getMessage();
			return new ErrorResponse(status.value(), message, info);
		}
	}
}
