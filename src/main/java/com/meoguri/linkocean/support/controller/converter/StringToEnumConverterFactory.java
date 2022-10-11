package com.meoguri.linkocean.support.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

	@Override
	public <T extends Enum<?>> Converter<String, T> getConverter(@NonNull final Class<T> targetType) {
		return new StringToEnumConverter<>(targetType);
	}

	private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

		private final Class<T> enumType;

		public StringToEnumConverter(final Class<T> enumType) {
			this.enumType = enumType;
		}

		@Override
		public T convert(final String source) {
			@SuppressWarnings("type safe") final T result = (T)Enum.valueOf(this.enumType, source.toUpperCase());
			return result;
		}
	}
}
