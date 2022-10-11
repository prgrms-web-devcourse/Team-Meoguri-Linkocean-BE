package com.meoguri.linkocean.support.controller.dto;

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

	public SliceResponse(final String name, final List<T> data, final boolean hasNext) {
		this.name = name;
		this.data = data;
		this.hasNext = hasNext;
	}

	public static <T> SliceResponse<T> of(final String name, final List<T> data, final boolean hasNext) {
		return new SliceResponse<>(name, data, hasNext);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
