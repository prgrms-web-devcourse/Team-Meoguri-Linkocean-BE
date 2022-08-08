package com.meoguri.linkocean.common;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableTypeAssert;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

public class LinkoceanAssert extends Assertions {
	public static ThrowableTypeAssert<LinkoceanRuntimeException> assertThatLinkoceanRuntimeException() {
		return assertThatExceptionOfType(LinkoceanRuntimeException.class);
	}
}
