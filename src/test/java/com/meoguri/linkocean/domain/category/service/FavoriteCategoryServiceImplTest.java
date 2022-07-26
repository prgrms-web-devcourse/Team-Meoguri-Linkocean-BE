package com.meoguri.linkocean.domain.category.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.category.service.dto.AddFavoriteCategoriesCommand;

@Sql("classpath:db/sql/InsertCategories.sql")
@Transactional
@SpringBootTest
class FavoriteCategoryServiceImplTest {

	@Autowired
	private FavoriteCategoryService favoriteCategoryService;

	@BeforeEach
	void setUp() {
		//TODO - User, Profile 하나씩 추가
	}

	// TODO - 프로필 서비스 구현 이후 완성
	@Test
	void 선호_카테고리_추가_성공() {
		//given
		favoriteCategoryService.addFavoriteCategories(
			new AddFavoriteCategoriesCommand(
				1L,
				List.of("자기계발", "인문")
			)
		);
	}
}
