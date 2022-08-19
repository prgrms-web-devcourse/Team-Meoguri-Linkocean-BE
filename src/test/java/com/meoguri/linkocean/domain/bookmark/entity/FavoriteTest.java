package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.profile.entity.Profile;

class FavoriteTest {

	@Test
	void 페이보릿_생성_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final Profile owner = createProfile();

		//when
		final Favorite favorite = new Favorite(bookmark, owner);

		//then
		assertThat(favorite).isNotNull()
			.extracting(Favorite::getBookmark, Favorite::getProfile)
			.containsExactly(bookmark, owner);
	}
}
