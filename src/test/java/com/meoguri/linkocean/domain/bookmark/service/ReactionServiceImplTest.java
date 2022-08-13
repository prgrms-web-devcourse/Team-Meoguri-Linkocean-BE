package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.ReactionCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class ReactionServiceImplTest {
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ReactionRepository reactionRepository;

	@Autowired
	private ReactionService reactionService;

	@PersistenceContext
	private EntityManager em;

	private User user1;
	private User user2;
	private Profile profile1;
	private Profile profile2;
	private LinkMetadata link;
	private Bookmark bookmark1;
	private Bookmark bookmark2;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 링크 메타 데이터 셋업
		user1 = userRepository.save(createUser("haha@gmail.com", "GOOGLE"));
		user2 = userRepository.save(createUser("gaga@naver.com", "NAVER"));

		profile1 = profileRepository.save(createProfile(user1, "haha"));
		profile2 = profileRepository.save(createProfile(user2, "gaga"));

		user1.registerProfile(profile1);
		user2.registerProfile(profile2);

		link = linkMetadataRepository.save(createLinkMetadata());

		bookmark1 = bookmarkRepository.save(createBookmark(profile1, link));
		bookmark2 = bookmarkRepository.save(createBookmark(profile2, link));
	}

	@Test
	void 리액션_등록_성공() {
		//사용자가_하나의_북마크에_대해_리액션을_처음_등록하는_경우
		final ReactionCommand reactionCommand = new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "like");

		//when
		reactionService.requestReaction(reactionCommand);

		em.flush();
		em.clear();

		final Optional<Reaction> findReaction = reactionRepository.findByProfile_idAndBookmark(profile1.getId(),
			bookmark2);

		//then
		assertThat(findReaction).isPresent().get()
			.extracting(Reaction::getProfile, Reaction::getBookmark, Reaction::getType)
			.containsExactly(profile1, bookmark2, "like");
	}

	@Test
	void 리액션_취소() {

		//이미_있는_리액션이_요청의_리액션_상태가_같음

		//given
		reactionRepository.save(new Reaction(profile1, bookmark2, Reaction.ReactionType.HATE.toString()));

		em.flush();
		em.clear();

		//when
		final ReactionCommand command = new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "hate");
		reactionService.requestReaction(command); //취소됨

		em.flush();
		em.clear();

		//then
		final Optional<Reaction> findReaction = reactionRepository.findByProfile_idAndBookmark(profile1.getId(),
			bookmark2);
		assertThat(findReaction).isEmpty();
	}

	@Test
	void 리액션_취소_후_등록() {

		//이미_있는_리액션이_요청의_리액션_상태가_다름

		//given
		reactionRepository.save(new Reaction(profile1, bookmark2, Reaction.ReactionType.HATE.toString()));

		em.flush();
		em.clear();
		//when
		final ReactionCommand command = new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "like");
		reactionService.requestReaction(command); //취소 후 등록

		em.flush();
		em.clear();

		//then
		final Optional<Reaction> findReaction = reactionRepository.findByProfile_idAndBookmark(profile1.getId(),
			bookmark2);

		assertThat(findReaction).isPresent().get()
			.extracting(Reaction::getProfile, Reaction::getBookmark, Reaction::getType)
			.containsExactly(profile1, bookmark2, "like");
	}

	@Test
	void 좋아요_개수_수정_성공_Like_추가_경우() {
		//given

		//when
		reactionService.requestReaction(new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "like"));

		em.flush();
		em.clear();

		//then
		assertThat(bookmark2.getLikeCount()).isEqualTo(1);
	}

	@Test
	void 좋아요_개수_수정_성공_Hate_추가_경우() {
		//given

		//when
		reactionService.requestReaction(new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "hate"));

		em.flush();
		em.clear();

		//then
		assertThat(bookmark2.getLikeCount()).isEqualTo(0);
	}

	@Test
	void 좋아요_개수_수정_성공_좋아요_좋아요_경우() {
		//given
		reactionRepository.save(new Reaction(profile1, bookmark2, Reaction.ReactionType.LIKE.toString()));
		bookmarkRepository.addBookmarkLikeCount(bookmark2.getId(), 1L);

		//when
		reactionService.requestReaction(new ReactionCommand(user1.getProfileId(), bookmark2.getId(), "like"));

		em.flush();
		em.clear();

		//then
		assertThat(bookmark2.getLikeCount()).isEqualTo(0);
	}

	@Test
	void 좋아요_개수_수정_성공_싫어요_좋아요_경우() {
		//given
		reactionRepository.save(new Reaction(profile1, bookmark2, Reaction.ReactionType.HATE.toString()));

		//when
		reactionService.requestReaction(new ReactionCommand(user1.getId(), bookmark2.getId(), "like"));

		em.flush();
		em.clear();

		//then
		assertThat(bookmark2.getLikeCount()).isEqualTo(1);
	}
}
