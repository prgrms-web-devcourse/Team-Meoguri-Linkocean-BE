package com.meoguri.linkocean.exception;

/**
 * OAuth 과정에서 발생하는 예외
 */
public class OAuthException extends RuntimeException {

	public OAuthException(final String message) {
		super(message);
	}
}
