package com.meoguri.linkocean.configuration.resolver;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

/**
 * String source 를 Enum ProfileSearchTab 으로 컨버팅 해준다.
 */
public class ProfileSearchTabConverterFactory implements ConverterFactory<String, Enum<? extends ProfileSearchTab>> {

	@Override
	public <T extends Enum<? extends ProfileSearchTab>> Converter<String, T> getConverter(final Class<T> targetType) {
		return source -> Arrays.stream(targetType.getEnumConstants())
			.filter(c -> c.name().equals(source.toUpperCase())).findAny()
			.orElseThrow(LinkoceanRuntimeException::new);
	}
}
