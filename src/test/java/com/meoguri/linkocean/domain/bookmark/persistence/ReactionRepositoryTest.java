package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class ReactionRepositoryTest {

	@Autowired
	private ReactionRepository reactionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@PersistenceContext
	private EntityManager em;

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
	void 프로필_북마크_조합은_유니크하다() {
		//given
		reactionRepository.save(new Reaction(profile, bookmark, "like"));

		//when then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> reactionRepository.save(new Reaction(profile, bookmark, "hate")));
	}

	@Test
	void 프로필_북마크_리액션타입_조합으로_삭제할수_있다() {
		//given
		final Reaction reaction = reactionRepository.save(new Reaction(profile, bookmark, "like"));
		em.flush();
		em.clear();

		//when
		final boolean isDeleted
			= reactionRepository.deleteByProfileAndBookmarkAndType(profile, bookmark, ReactionType.LIKE) > 0;
		em.flush(); // <- delete 쿼리 발생 어떻게 id 에 대한 in 절 쿼리가 나가는지 의문
		em.clear();

		//then
		assertThat(isDeleted).isTrue();
		assertThat(reactionRepository.findById(reaction.getId())).isEmpty();

		em.flush();
		em.clear();
	}

	@Test
	void 리액션_별_카운트를_조회할_수_있다() {
		//given
		final User user1 = userRepository.save(createUser("test@gmail.com", "GOOGLE"));
		final Profile profile1 = profileRepository.save(createProfile(user1, "test"));

		reactionRepository.save(new Reaction(profile, bookmark, ReactionType.LIKE.name()));
		reactionRepository.save(new Reaction(profile1, bookmark, ReactionType.HATE.name()));

		//when
		final Map<ReactionType, Long> group = reactionRepository.countReactionGroup(bookmark);

		//then
		assertThat(group.getOrDefault(ReactionType.LIKE, 0L)).isEqualTo(1);
		assertThat(group.getOrDefault(ReactionType.HATE, 0L)).isEqualTo(1);
	}

}
