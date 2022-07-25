package com.meoguri.linkocean.domain.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.category.entity.Category;

@Sql("classpath:db/sql/InsertCategories.sql")
@Transactional
@SpringBootTest(webEnvironment = NONE)
class CategoryServiceImplTest {

	@Autowired
	private CategoryService categoryService;

	@Test
	void 이름_목록으로_조회_성공() {
		//given
		final String name1 = "자기계발";
		final String name2 = "인문";

		//when
		final List<Category> categories = categoryService.findByNames(List.of(name1, name2));

		//then
		assertThat(categories)
			.extracting(Category::getName)
			.containsExactlyInAnyOrder(name1, name2);
	}
}
