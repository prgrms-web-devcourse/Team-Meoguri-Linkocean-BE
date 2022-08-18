package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Import(ReactionQuery.class)
@DataJpaTest
class ReactionQueryTest {

	@Autowired
	private ReactionQuery reactionQuery;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ReactionRepository reactionRepository;

	private Profile profile;
	private Bookmark bookmark;

	@BeforeEach
	void setUp() {
		profile = profileRepository.save(new Profile(userRepository.save(createUser()), "haha"));
		bookmark = bookmarkRepository.save(new Bookmark(
			profile,
			linkMetadataRepository.save(createLinkMetadata()),
			"title",
			"memo",
			OpenType.ALL,
			Category.HEALTH,
			"www.google.com",
			Collections.emptyList()
		));
	}

	@Test
	void 리액션_별_카운트를_조회할_수_있다() {
		//given
		final User user1 = userRepository.save(createUser("test@gmail.com", GOOGLE));
		final Profile profile1 = profileRepository.save(createProfile(user1, "test"));

		reactionRepository.save(new Reaction(profile, bookmark, ReactionType.LIKE));
		reactionRepository.save(new Reaction(profile1, bookmark, ReactionType.HATE));

		//when
		final Map<ReactionType, Long> reactionCountMap = reactionQuery.getReactionCountMap(bookmark);

		//then
		assertThat(reactionCountMap.get(ReactionType.LIKE)).isEqualTo(1);
		assertThat(reactionCountMap.get(ReactionType.HATE)).isEqualTo(1);
	}

	@Test
	void 리액션_여부_맵을_조회할_수_있다() {
		//given
		reactionRepository.save(new Reaction(profile, bookmark, ReactionType.LIKE));

		//when
		final Map<ReactionType, Boolean> reactionMap = reactionQuery.getReactionMap(profile.getId(), bookmark);

		//then
		assertThat(reactionMap.get(ReactionType.LIKE)).isTrue();
		assertThat(reactionMap.get(ReactionType.HATE)).isFalse();
	}
}
