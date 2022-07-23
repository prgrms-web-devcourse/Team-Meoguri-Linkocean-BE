package com.meoguri.linkocean.domain.reaction.entity;

import static com.meoguri.linkocean.domain.reaction.entity.Reaction.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

class ReactionTest {

	@Test
	void 리액션_생성_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final ReactionType type = ReactionType.LIKE;

		//when
		final Reaction reaction = new Reaction(bookmark, type);

		//then
		assertThat(reaction).isNotNull()
			.extracting(Reaction::getBookmark, Reaction::getType)
			.containsExactly(bookmark, type);
	}
}
