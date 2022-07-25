package com.meoguri.linkocean.domain.category.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategoryTest {

	@Test
	void 카테고리_생성_성공() {
		//given
		final String name = "카테고리";

		//when
		final Category category = new Category(name);

		//then
		assertThat(category.getName()).isEqualTo(name);
	}
}
