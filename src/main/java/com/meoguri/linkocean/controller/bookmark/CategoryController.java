package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.common.ListResponse;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	@GetMapping
	public ListResponse<String> getAllCategories() {
		return ListResponse.of("categories", Category.getKoreanNames());
	}
}
