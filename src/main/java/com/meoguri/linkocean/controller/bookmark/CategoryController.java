package com.meoguri.linkocean.controller.bookmark;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

	@GetMapping
	public SliceResponse<String> getAllCategories() {

		return SliceResponse.of("categories", Bookmark.Category.getKoreanNames());
	}
}
