package com.meoguri.linkocean.domain.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.category.entity.Category;

@DataJpaTest
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	void 이름으로_조회_성공() {
		//given
		final String name = "카테고리";
		categoryRepository.save(new Category(name));

		//when
		final Optional<Category> oCategory = categoryRepository.findByName(name);

		//then
		assertThat(oCategory).isPresent();
		assertThat(oCategory.get().getName()).isEqualTo(name);
	}

	@Test
	void 이름_목록으로_조회_성공() {
		final String name1 = "카테고리1";
		final String name2 = "카테고리2";
		categoryRepository.save(new Category(name1));
		categoryRepository.save(new Category(name2));

		final List<Category> categories = categoryRepository.findByNameIn(List.of(name1, name2));

		assertThat(categories)
			.extracting(Category::getName)
			.containsExactly(name1, name2);

	}
}