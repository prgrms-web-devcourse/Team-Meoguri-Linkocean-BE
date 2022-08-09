package com.meoguri.linkocean.configuration.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

@Converter(autoApply = true)
public class OpenTypeConverter implements AttributeConverter<OpenType, Byte> {

	@Override
	public Byte convertToDatabaseColumn(final OpenType attribute) {
		return attribute.getCode();
	}

	@Override
	public OpenType convertToEntityAttribute(final Byte dbData) {
		return Stream.of(OpenType.values())
			.filter(ot -> ot.getCode() == dbData)
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}
}