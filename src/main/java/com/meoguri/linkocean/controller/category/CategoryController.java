package com.meoguri.linkocean.controller.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public SliceResponse<String> getAllCategories() {

		return SliceResponse.of("categories", categoryService.getAllCategories());
	}
}
