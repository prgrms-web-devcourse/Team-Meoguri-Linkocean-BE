package com.meoguri.linkocean.domain.category.service;

import com.meoguri.linkocean.domain.category.service.dto.AddFavoriteCategoriesCommand;

public interface FavoriteCategoryService {

	/**
	 * 회원 가입시 선호 카테고리를 채우기 위해 사용
	 */
	void addFavoriteCategories(AddFavoriteCategoriesCommand command);

}
