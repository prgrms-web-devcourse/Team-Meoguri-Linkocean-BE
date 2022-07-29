package com.meoguri.linkocean.controller.category.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class AllCategoriesResponse {

	private final int count;
	private final List<String> categories;

	public AllCategoriesResponse(final List<String> categories) {

		this.count = categories.size();
		this.categories = categories;
	}
}
