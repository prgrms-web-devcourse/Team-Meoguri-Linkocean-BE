package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

@SpringBootTest
@Transactional
class BookmarkServiceImplTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@PersistenceContext
	private EntityManager em;

	private long userId;
	private Profile profile;
	private LinkMetadata linkMetadata;
	private String url;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 링크 메타 데이터 셋업
		User user = userRepository.save(createUser());
		userId = user.getId();

		profile = profileRepository.save(createProfile(user));
		linkMetadata = linkMetadataRepository.save(createLinkMetadata());

		url = linkMetadata.getSavedUrl(); // 조회를 위해서는 저장된 url 이 필요하다
	}

	@Test
	void 북마크_등록_성공() {
		//given
		final RegisterBookmarkCommand command =

			new RegisterBookmarkCommand(userId, url, "title", "memo", "인문", "all", List.of("tag1", "tag2"));

		//when
		final long savedBookmarkId = bookmarkService.registerBookmark(command);

		em.flush();
		em.clear();

		//then
		final Optional<Bookmark> oBookmark = bookmarkRepository.findById(savedBookmarkId);
		assertThat(oBookmark).isPresent().get()
			.extracting(
				Bookmark::getProfile,
				Bookmark::getLinkMetadata,
				Bookmark::getTitle,
				Bookmark::getMemo,
				Bookmark::getCategory,
				Bookmark::getOpenType
			).containsExactly(
				profile,
				linkMetadata,
				command.getTitle(),
				command.getMemo(),
				command.getCategory(),
				command.getOpenType()
			);
		assertThat(oBookmark.get().getTagNames())
			.hasSameElementsAs(command.getTagNames());
	}

	@Test
	void 북마크_생성_실패_유효하지_않은_사용자() {
		//given
		final long invalidId = 10L;
		final RegisterBookmarkCommand command = command(invalidId, url);

		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> bookmarkService.registerBookmark(command));
	}

	@Test
	void 중복_url_북마크_생성_요청에_따라_실패() {
		//given
		final Bookmark bookmark = createBookmark(profile, linkMetadata);
		bookmarkRepository.save(bookmark);

		/* 중복 url 생성 요청 */
		final RegisterBookmarkCommand registerBookmarkCommand = command(userId, url);

		//when then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> bookmarkService.registerBookmark(registerBookmarkCommand));
	}

	private RegisterBookmarkCommand command(long userId, final String url) {
		return new RegisterBookmarkCommand(userId, url, null, null, null, "all", emptyList());
	}

	@Nested
	class 북마크_업데이트_테스트 {

		private Bookmark bookmark;

		@BeforeEach
		void setUp() {
			bookmark = bookmarkRepository.save(createBookmark(profile, linkMetadata));
		}

		@Test
		void 북마크_업데이트_성공() {
			//given
			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				userId,
				bookmark.getId(),
				"updatedTitle",
				"updatedMemo",
				"인문",
				"private",
				List.of("tag1", "tag2")
			);

			//when
			bookmarkService.updateBookmark(command);

			//then
			em.flush();
			em.clear();

			final Bookmark updatedBookmark = bookmarkRepository.findById(command.getBookmarkId()).get();
			assertThat(updatedBookmark).isNotNull()
				.extracting(
					Bookmark::getTitle,
					Bookmark::getMemo,
					Bookmark::getCategory,
					Bookmark::getOpenType
				).containsExactly(
					command.getTitle(),
					command.getMemo(),
					command.getCategory(),
					command.getOpenType()
				);

			assertThat(bookmark.getTagNames()).hasSize(2)
				.containsExactly(command.getTagNames().get(0), command.getTagNames().get(1));
		}

		@Test
		void 북마크_업데이트_실패_존재하지_않는_사용자() {
			//given
			final long invalidId = 10L;
			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				invalidId,
				bookmark.getId(),
				"updatedTitle",
				"updatedMemo",
				"home",
				"private",
				List.of("tag1", "tag2")
			);

			//when then
			assertThatExceptionOfType(LinkoceanRuntimeException.class)
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		@Test
		void 북마크_업데이트_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = 10L;
			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				userId,
				invalidBookmarkId,
				"updatedTitle",
				"updatedMemo",
				"home",
				"private",
				List.of("tag1", "tag2")
			);

			//when then
			assertThatExceptionOfType(LinkoceanRuntimeException.class)
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		@Test
		void 북마크_업데이트_실패_다른_사용자의_북마크_조회() {
			//given
			final User anotherUser = createUser("crush@mail.com", "NAVER");
			userRepository.save(anotherUser);

			final Profile anotherProfile = createProfile(anotherUser, "crush");
			profileRepository.save(anotherProfile);

			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				anotherUser.getId(),
				bookmark.getId(),
				"updatedTitle",
				"updatedMemo",
				"home",
				"private",
				List.of("tag1", "tag2")
			);

			//when then
			assertThatExceptionOfType(LinkoceanRuntimeException.class)
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}
	}
}
