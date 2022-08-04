package com.meoguri.linkocean.controller.common;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

@Getter
@JsonSerialize(using = PageResponseJsonSerializer.class)
public class PageResponse<T> {

	private final long totalCount;
	private final String name;
	private final List<T> data;

	public PageResponse(final long totalCount, final String name, final List<T> data) {
		this.totalCount = totalCount;
		this.name = name;
		this.data = data;
	}

	public static <T> PageResponse<T> of(final long totalCount, final String name, final List<T> data) {
		return new PageResponse<>(totalCount, name, data);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
