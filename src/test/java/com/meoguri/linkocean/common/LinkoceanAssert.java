package com.meoguri.linkocean.common;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.ThrowableTypeAssert;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

public final class LinkoceanAssert {
	public static ThrowableTypeAssert<LinkoceanRuntimeException> assertThatLinkoceanRuntimeException() {
		return assertThatExceptionOfType(LinkoceanRuntimeException.class);
	}
}
