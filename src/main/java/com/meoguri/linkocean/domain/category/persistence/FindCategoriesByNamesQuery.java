package com.meoguri.linkocean.domain.category.persistence;

import java.util.List;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.category.entity.Category;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindCategoriesByNamesQuery {

	private final CategoryRepository categoryRepository;

	public List<Category> findByNames(final List<String> names) {

		return categoryRepository.findByNameIn(names);
	}

}
