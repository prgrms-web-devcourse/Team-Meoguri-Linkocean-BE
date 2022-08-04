package com.meoguri.linkocean.controller.common;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

@Getter
@JsonSerialize(using = PageResponseJsonSerializer.class)
public class PageResponse<T> {

	private final int count;
	private final String name;
	private final List<T> data;

	public PageResponse(final String name, final List<T> data) {
		this.count = data.size();
		this.name = name;
		this.data = data;
	}

	public static <T> PageResponse<T> of(final String name, final List<T> data) {
		return new PageResponse<>(name, data);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
