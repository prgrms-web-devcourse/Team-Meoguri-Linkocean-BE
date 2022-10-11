package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.support.controller.dto.ListResponse;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	@GetMapping
	public ListResponse<String> getAllCategories() {
		return ListResponse.of("categories", Category.getKoreanNames());
	}
}
