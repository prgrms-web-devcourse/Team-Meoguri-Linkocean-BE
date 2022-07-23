package com.meoguri.linkocean.exception;

import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import javax.annotation.CheckForNull;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Preconditions {

	public static void checkNullableStringLength(final String target, final int maxLength, final String message,
		Object... args) {
		if (hasText(target)) {
			checkArgument(
				target.length() <= maxLength,
				String.format(message, args)
			);
		}
	}

	public static void checkNotNullStringLength(final String target, final int maxLength, final String message,
		Object... args) {
		checkArgument(hasText(target), message);
		checkArgument(target.length() <= maxLength,
			String.format(message, args));
	}

	public static void checkArgument(boolean expression, @CheckForNull Object errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}
	}

	public static void checkState(boolean expression, @CheckForNull Object errorMessage) {
		if (!expression) {
			throw new IllegalStateException(String.valueOf(errorMessage));
		}
	}
}
