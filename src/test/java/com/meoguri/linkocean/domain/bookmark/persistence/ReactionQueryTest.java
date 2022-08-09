package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
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
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

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
		bookmark = bookmarkRepository.save(
			Bookmark.builder()
				.profile(profile)
				.linkMetadata(linkMetadataRepository.save(createLinkMetadata()))
				.title("title")
				.memo("memo")
				.category("인문")
				.openType("all")
				.url("www.google.com")
				.tags(Collections.emptyList())
				.build());
	}

	@Test
	void 리액션_별_카운트를_조회할_수_있다() {
		//given
		final User user1 = userRepository.save(createUser("test@gmail.com", "GOOGLE"));
		final Profile profile1 = profileRepository.save(createProfile(user1, "test"));

		reactionRepository.save(new Reaction(profile, bookmark, Reaction.ReactionType.LIKE.name()));
		reactionRepository.save(new Reaction(profile1, bookmark, Reaction.ReactionType.HATE.name()));

		//when
		final Map<Reaction.ReactionType, Long> reactionCountMap = reactionQuery.getReactionCountMap(bookmark);

		//then
		assertThat(reactionCountMap.get(Reaction.ReactionType.LIKE)).isEqualTo(1);
		assertThat(reactionCountMap.get(Reaction.ReactionType.HATE)).isEqualTo(1);
	}

	@Test
	void 리액션_여부_맵을_조회할_수_있다() {
		//given
		reactionRepository.save(new Reaction(profile, bookmark, Reaction.ReactionType.LIKE.name()));

		//when
		final Map<Reaction.ReactionType, Boolean> reactionMap = reactionQuery.getReactionMap(profile, bookmark);

		//then
		assertThat(reactionMap.get(Reaction.ReactionType.LIKE)).isTrue();
		assertThat(reactionMap.get(Reaction.ReactionType.HATE)).isFalse();
	}
}
