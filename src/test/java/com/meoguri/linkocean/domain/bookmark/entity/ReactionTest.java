package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

class ReactionTest {

	@Test
	void 리액션_생성_성공() {
		//given
		final Profile profile = createProfile();
		final Bookmark bookmark = createBookmark();
		final ReactionType reactionType = LIKE;

		//when
		final Reaction reaction = new Reaction(profile, bookmark, reactionType);

		//then
		assertThat(reaction).isNotNull()
			.extracting(Reaction::getBookmark, Reaction::getType)
			.containsExactly(bookmark, reactionType);
	}
}
