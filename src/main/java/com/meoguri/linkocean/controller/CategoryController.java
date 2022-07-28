package com.meoguri.linkocean.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.dto.category.AllCategoriesResponse;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public AllCategoriesResponse getAllCategories() {

		return new AllCategoriesResponse(categoryService.getAllCategories());
	}
}
