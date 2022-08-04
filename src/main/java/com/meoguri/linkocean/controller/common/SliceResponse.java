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

	public SliceResponse(final String name, final List<T> data) {
		this.name = name;
		this.data = data;
	}

	public static <T> SliceResponse<T> of(final String name, final List<T> data) {
		return new SliceResponse<>(name, data);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
