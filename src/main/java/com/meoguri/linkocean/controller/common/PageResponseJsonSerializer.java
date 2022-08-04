package com.meoguri.linkocean.controller.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 목록 조회의 공통 Serializer
 * - 데이터 타입 별로 array 의 이름을 커스터마이징 하기 위해 사용
 */
public class PageResponseJsonSerializer extends JsonSerializer<PageResponse<?>> {

	@Override
	public void serialize(final PageResponse value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {

		gen.writeStartObject();
		gen.writeFieldName("totalCount");
		gen.writeNumber(value.getTotalCount());
		gen.writeArrayFieldStart(value.getName());

		for (Object datum : value.getData()) {
			gen.writeObject(datum);
		}

		gen.writeEndArray();
		gen.writeEndObject();
	}

}
