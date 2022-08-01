package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

class FavoriteCategoryTest {

	@Test
	void 선호_카테고리_생성_성공() {
		//given
		final Profile profile = createProfile();
		final String category = "인문";

		//when
		final FavoriteCategory favoriteCategory = new FavoriteCategory(profile, category);

		//then
		assertThat(favoriteCategory).isNotNull()
			.extracting(FavoriteCategory::getProfile, FavoriteCategory::getCategory)
			.containsExactly(profile, Bookmark.Category.of(category));
	}

	@Test
	void 정의되지_않은_카테고리로_선호_카테고리_생성_실패() {
		//given
		final Profile profile = createProfile();
		final String category = "undefined category";

		//when then
		assertThatThrownBy(() -> new FavoriteCategory(profile, category))
			.isInstanceOf(LinkoceanRuntimeException.class);
	}
}
