package com.meoguri.linkocean.support.controller.dto;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

@Getter
@JsonSerialize(using = ListResponseJsonSerializer.class)
public class ListResponse<T> {
	private final String name;
	private final List<T> data;

	public ListResponse(final String name, final List<T> data) {
		this.name = name;
		this.data = data;
	}

	public static <T> ListResponse<T> of(final String name, final List<T> data) {
		return new ListResponse<>(name, data);
	}

	public List<Object> getData() {
		return Arrays.asList(data.toArray());
	}
}
