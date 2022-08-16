package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
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
	private ReactionService reactionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private long profileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 링크 메타 데이터 셋업
		User user = userRepository.save(createUser("haha@gmail.com", "GOOGLE"));
		final Profile profile = profileRepository.save(createProfile(user, "haha"));
		user.registerProfile(profile);
		profileId = profile.getId();

		LinkMetadata link = linkMetadataRepository.save(createLinkMetadata());
		bookmarkId = bookmarkRepository.save(createBookmark(profile, link)).getId();
	}

	@Test
	void 리액션_요청_등록_성공() {
		//given
		final ReactionCommand reactionCommand = new ReactionCommand(profileId, bookmarkId, LIKE);

		//when
		reactionService.requestReaction(reactionCommand);

		//then
		final Optional<Bookmark> likeAddedBookmark = bookmarkRepository.findById(bookmarkId);
		assertThat(likeAddedBookmark.get().getLikeCount()).isEqualTo(1);
	}

	@Test
	void 리액션_요청_취소_성공() {
		//given
		final ReactionCommand likeCommand = new ReactionCommand(profileId, bookmarkId, LIKE);
		reactionService.requestReaction(likeCommand);

		final Optional<Bookmark> likeAddedBookmark = bookmarkRepository.findById(bookmarkId);
		assertThat(likeAddedBookmark.get().getLikeCount()).isEqualTo(1);

		//when
		reactionService.requestReaction(likeCommand);

		//then
		final Optional<Bookmark> likeCancelledBookmark = bookmarkRepository.findById(bookmarkId);
		assertThat(likeCancelledBookmark.get().getLikeCount()).isEqualTo(0);
	}

	@Test
	void 리액션_요청_변경_성공() {
		//given
		reactionService.requestReaction(new ReactionCommand(profileId, bookmarkId, HATE));

		//when
		reactionService.requestReaction(new ReactionCommand(profileId, bookmarkId, LIKE));

		//then
		final Optional<Bookmark> likeAddedBookmark = bookmarkRepository.findById(bookmarkId);
		assertThat(likeAddedBookmark.get().getLikeCount()).isEqualTo(1);
	}
}
