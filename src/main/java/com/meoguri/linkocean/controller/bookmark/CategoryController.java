package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	@GetMapping
	public SliceResponse<String> getAllCategories() {

		return SliceResponse.of("categories", Category.getKoreanNames());
	}
}
