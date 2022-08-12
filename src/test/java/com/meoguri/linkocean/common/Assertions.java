package com.meoguri.linkocean.common;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.ThrowableTypeAssert;
import org.springframework.dao.DataIntegrityViolationException;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

public final class Assertions {
	public static ThrowableTypeAssert<LinkoceanRuntimeException> assertThatLinkoceanRuntimeException() {
		return assertThatExceptionOfType(LinkoceanRuntimeException.class);
	}

	public static ThrowableTypeAssert<DataIntegrityViolationException> assertThatDataIntegrityViolationException() {
		return assertThatExceptionOfType(DataIntegrityViolationException.class);
	}

}
