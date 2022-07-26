package com.meoguri.linkocean.domain.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.category.entity.Category;
import com.meoguri.linkocean.domain.category.persistence.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	@Override
	public List<Category> findByNames(final List<String> names) {

		return categoryRepository.findByNameIn(names);
	}

}
