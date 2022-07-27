package com.meoguri.linkocean.domain.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.category.entity.FavoriteCategory;
import com.meoguri.linkocean.domain.category.persistence.FavoriteCategoryRepository;
import com.meoguri.linkocean.domain.category.persistence.FindCategoriesByNamesQuery;
import com.meoguri.linkocean.domain.category.service.dto.AddFavoriteCategoriesCommand;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteCategoryServiceImpl implements FavoriteCategoryService {

	private final FindCategoriesByNamesQuery findCategoriesByNamesQuery;
	private final FavoriteCategoryRepository favoriteCategoryRepository;

	@Override
	public void addFavoriteCategories(final AddFavoriteCategoriesCommand command) {

		final List<String> categoryNames = command.getCategoryNames();

		//TODO - Profile 서비스 구현 후 profileId 로 Profile 조회 하여 채우기 지금은 임시로 null
		findCategoriesByNamesQuery.findByNames(categoryNames)
			.forEach(c -> favoriteCategoryRepository.save(new FavoriteCategory(null, c)));
	}
}
