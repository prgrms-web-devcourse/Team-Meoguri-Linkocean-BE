package com.meoguri.linkocean.domain.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import com.meoguri.linkocean.domain.category.entity.Category;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("classpath:db/sql/InsertCategories.sql")
@DataJpaTest
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@AfterAll
	void setUp() {
		categoryRepository.deleteAllInBatch();
	}

	@Test
	void 이름_목록으로_조회_성공() {
		//given
		final String name1 = "자기계발";
		final String name2 = "인문";

		//when
		final List<Category> categories = categoryRepository.findByNameIn(List.of(name1, name2));

		//then
		assertThat(categories)
			.extracting(Category::getName)
			.containsExactlyInAnyOrder(name1, name2);
	}
}
