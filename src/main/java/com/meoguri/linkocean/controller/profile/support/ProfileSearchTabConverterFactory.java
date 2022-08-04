package com.meoguri.linkocean.controller.profile.support;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static org.flywaydb.core.internal.util.StringUtils.*;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

/**
 * String source 를 Enum ProfileSearchTab 으로 컨버팅 해준다.
 * tab 은 선언되면 항상 사용 되기 때문에 존재하지 않으면 예외 처리한다.
 */
public class ProfileSearchTabConverterFactory implements ConverterFactory<String, Enum<? extends ProfileSearchTab>> {

	@Override
	public <T extends Enum<? extends ProfileSearchTab>> Converter<String, T> getConverter(final Class<T> targetType) {
		return source -> {
			checkCondition(hasText(source));

			return Arrays.stream(targetType.getEnumConstants())
				.filter(c -> c.name().equals(source.toUpperCase())).findAny()
				.orElseThrow(LinkoceanRuntimeException::new);
		};
	}

}
