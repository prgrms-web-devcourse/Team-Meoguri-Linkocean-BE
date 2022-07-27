package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

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
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class ReactionRepositoryTest {

	@PersistenceContext
	private EntityManager em;

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
				.openType(ALL)
				.build());
	}

	@Test
	void 프로필_북마크_조합은_유니크하다() {
		//given
		reactionRepository.save(new Reaction(profile, bookmark, ReactionType.LIKE));

		//when then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> reactionRepository.save(new Reaction(profile, bookmark, ReactionType.HATE)));
	}

	@Test
	void 프로필_북마크_리액션타입_조합으로_삭제할수_있다() {
		//given
		final Reaction reaction = reactionRepository.save(new Reaction(profile, bookmark, ReactionType.LIKE));
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
}
