package com.meoguri.linkocean.controller.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.common.PageResponse;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public PageResponse<String> getAllCategories() {

		return PageResponse.of("categories", categoryService.getAllCategories());
	}
}
