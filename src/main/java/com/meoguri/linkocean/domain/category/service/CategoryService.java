package com.meoguri.linkocean.domain.category.service;

import java.util.List;

import com.meoguri.linkocean.domain.category.entity.Category;

public interface CategoryService {

	/**
	 * 이름 목록으로 카테고리 조회
	 * - 사실상 카테고리의 ID (연관)을 조회 하기 위해서만 사용
	 */
	List<Category> findByNames(List<String> names);
}
