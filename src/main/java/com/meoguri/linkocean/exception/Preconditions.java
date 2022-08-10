package com.meoguri.linkocean.exception;

import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Preconditions {

	public static void checkNullableStringLength(
		final String target, final int maxLength, final String errorMessage, final Object... args) {
		if (hasText(target)) {
			checkArgument(target.length() <= maxLength, String.format(errorMessage, args));
		}
	}

	public static void checkNotNullStringLength(
		final String target, final int maxLength, final String errorMessage, final Object... args) {
		checkArgument(hasText(target), errorMessage);
		checkArgument(target.length() <= maxLength, String.format(errorMessage, args));
	}

	public static void checkArgument(final boolean expression, final String errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	public static void checkState(final boolean expression, final String errorMessage) {
		if (!expression) {
			throw new IllegalStateException(errorMessage);
		}
	}

	public static void checkCondition(final boolean expression) {
		if (!expression) {
			throw new LinkoceanRuntimeException();
		}
	}

	public static void checkCondition(final boolean expression, final String errorMessage, final Object... args) {
		if (!expression) {
			throw new LinkoceanRuntimeException(String.format(errorMessage, args));
		}
	}

	public static void checkNotNull(final Object target) {
		if (target == null) {
			throw new NullPointerException();
		}
	}
}
