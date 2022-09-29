package com.meoguri.linkocean.support.controller.converter;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.converter.Converter;

import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

class StringToEnumConverterFactoryTest {

	private final StringToEnumConverterFactory factory = new StringToEnumConverterFactory();

	@ParameterizedTest
	@ValueSource(strings = {"google", "GOOGLE", "Google"})
	void 컨버팅_성공(final String source) {
		//given
		final Converter<String, OAuthType> converter = factory.getConverter(OAuthType.class);

		//when
		final OAuthType result = converter.convert(source);

		//then
		assertThat(result).isSameAs(GOOGLE);
	}
}
