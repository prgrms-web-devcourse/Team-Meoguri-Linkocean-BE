package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

class FavoriteCategoryTest {

	@Test
	void 선호_카테고리_생성_성공() {
		//given
		final Profile profile = createProfile();
		final Category category = HUMANITIES;

		//when
		final FavoriteCategory favoriteCategory = new FavoriteCategory(profile, category);

		//then
		assertThat(favoriteCategory).isNotNull()
			.extracting(FavoriteCategory::getProfile, FavoriteCategory::getCategory)
			.containsExactly(profile, category);
	}
}
