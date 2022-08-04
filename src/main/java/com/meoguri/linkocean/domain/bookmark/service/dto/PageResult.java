package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PageResult<T> {

	private final long totalCount;
	private final List<T> data;

	public PageResult(final long totalCount, final List<T> data) {
		this.totalCount = totalCount;
		this.data = data;
	}

	public static <T> PageResult<T> of(final long totalCount, final List<T> data) {
		return new PageResult<>(totalCount, data);
	}
}
