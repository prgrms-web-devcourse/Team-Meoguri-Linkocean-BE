package com.meoguri.linkocean.domain.category.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.category.repository.CategoryRepository;
import com.meoguri.linkocean.domain.category.service.dto.AddFavoriteCategoriesCommand;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("classpath:db/sql/InsertCategories.sql")
@Transactional
@SpringBootTest(webEnvironment = NONE)
class FavoriteCategoryServiceImplTest {

	@Autowired
	private FavoriteCategoryService favoriteCategoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@AfterAll
	void setUp() {
		categoryRepository.deleteAllInBatch();

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
