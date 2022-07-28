package com.meoguri.linkocean.domain.bookmark.service;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark.Category;

/**
 * Spring 에 의존적이지 않아 SpringBootTest 추가하지 않음
 */
class CategoryServiceImplTest {

	@Test
	void 카테고리_전체조회_성공() {
		//when
		final List<String> allCategories = new CategoryServiceImpl().getAllCategories();

		//then
		final List<String> expectedCategories
			= Arrays.stream(Category.values()).map(Category::getName).collect(toList());
		assertThat(allCategories)
			.containsExactlyElementsOf(expectedCategories);
	}
}
