package com.meoguri.linkocean.controller.common;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = PageResponseJsonSerializer.class)
public class PageResponse<T> {

	private final String name;
	private final List<T> data;
	private final long totalCount;

	public static <T> PageResponse<T> of(final String name, final List<T> data, final long totalCount) {
		return new PageResponse<>(name, data, totalCount);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
