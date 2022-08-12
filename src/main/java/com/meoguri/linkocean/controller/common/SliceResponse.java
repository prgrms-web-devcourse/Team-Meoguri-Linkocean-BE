package com.meoguri.linkocean.controller.common;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

@Getter
@JsonSerialize(using = SliceResponseJsonSerializer.class)
public class SliceResponse<T> {

	private final String name;
	private final List<T> data;
	private final Boolean hasNext;

	public SliceResponse(final String name, final List<T> data, final Boolean hasNext) {
		this.name = name;
		this.data = data;
		this.hasNext = hasNext;
	}

	public static <T> SliceResponse<T> of(final String name, final List<T> data, final Boolean hasNext) {
		return new SliceResponse<>(name, data, hasNext);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
