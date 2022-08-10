package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.common.LinkoceanAssert.*;
import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType.*;
import static com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.BookmarkStatus;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.FavoriteRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.ReactionRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.TagRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

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

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private ReactionRepository reactionRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private FollowRepository followRepository;

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

		url = linkMetadata.getSavedLink(); // 조회를 위해서는 저장된 url 이 필요하다
	}

	@Nested
	class 북마크_등록 {
		@Test
		void 북마크_등록_성공() {
			//given
			final RegisterBookmarkCommand command =

				new RegisterBookmarkCommand(userId, url, "title", "memo", Category.IT,
					OpenType.ALL, List.of("tag1", "tag2"));

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
		void 북마크_등록_실패_유효하지_않은_사용자() {
			//given
			final long invalidId = -1L;
			final RegisterBookmarkCommand command = command(invalidId, url);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.registerBookmark(command));
		}

		@Test
		void 중복_url_북마크_생성_요청에_따라_실패() {
			//given
			final RegisterBookmarkCommand command = command(userId, url);
			bookmarkService.registerBookmark(command);

			//when then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> bookmarkService.registerBookmark(command));
		}

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
				Category.HUMANITIES,
				OpenType.PRIVATE,
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
				Category.HUMANITIES,
				OpenType.PRIVATE,
				List.of("tag1", "tag2")
			);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

		@Test
		void 북마크_업데이트_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = -1L;
			final UpdateBookmarkCommand command = new UpdateBookmarkCommand(
				userId,
				invalidBookmarkId,
				"updatedTitle",
				"updatedMemo",
				Category.HUMANITIES,
				OpenType.PRIVATE,
				List.of("tag1", "tag2")
			);

			//when then
			assertThatLinkoceanRuntimeException()
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
				Category.HUMANITIES,
				OpenType.PRIVATE,
				List.of("tag1", "tag2")
			);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.updateBookmark(command));
		}

	}

	@Nested
	class 북마크_삭제 {

		private Bookmark bookmark;

		@BeforeEach
		void setUp() {
			bookmark = bookmarkRepository.save(createBookmark(profile, linkMetadata));
		}

		@Test
		void 북마크_삭제_성공() {
			//given, when
			bookmarkService.removeBookmark(userId, bookmark.getId());

			//then
			em.flush();
			em.clear();

			final Optional<Bookmark> updatedBookmark = bookmarkRepository.findById(bookmark.getId());
			assertThat(updatedBookmark.get().getStatus()).isEqualTo(BookmarkStatus.REMOVED);
		}

		@Test
		void 북마크_삭제_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = -1L;

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.removeBookmark(userId, invalidBookmarkId));
		}

		@Test
		void 북마크_삭제_실패_다른_사용자의_북마크_삭제_시도() {
			//given
			final User anotherUser = createUser("hani@mail.com", "NAVER");
			userRepository.save(anotherUser);

			final Profile anotherProfile = createProfile(anotherUser, "crush");
			profileRepository.save(anotherProfile);

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.removeBookmark(anotherUser.getId(), bookmark.getId()));
		}

	}

	@Nested
	class 북마크_상세_조회_테스트 {

		private Bookmark bookmark;

		@BeforeEach
		void setUp() {
			final Tag tag = tagRepository.save(new Tag("tag1"));

			bookmark = bookmarkRepository.save(new Bookmark(
				profile,
				linkMetadata,
				"title",
				"dream company",
				OpenType.ALL,
				Category.IT,
				"www.google.com",
				List.of(tag)
			));

			em.flush();
			em.clear();
		}

		@Test
		void 북마크_상세_조회_성공() {
			//when
			final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(userId, bookmark.getId());

			//then
			assertThat(result).extracting(
				GetDetailedBookmarkResult::getTitle,
				GetDetailedBookmarkResult::getUrl,
				GetDetailedBookmarkResult::getImage,
				GetDetailedBookmarkResult::getCategory,
				GetDetailedBookmarkResult::getMemo,
				GetDetailedBookmarkResult::getOpenType,
				GetDetailedBookmarkResult::isFavorite,
				GetDetailedBookmarkResult::getUpdatedAt
			).containsExactly(
				bookmark.getTitle(),
				bookmark.getUrl(),
				bookmark.getLinkMetadata().getImage(),
				bookmark.getCategory(),
				bookmark.getMemo(),
				bookmark.getOpenType(),
				false,
				bookmark.getUpdatedAt()
			);
			assertThat(result.getTags())
				.contains("tag1");

			assertThat(result.getReactionCount().get(LIKE)).isZero();
			assertThat(result.getReactionCount().get(HATE)).isZero();

			assertThat(result.getReaction().get(LIKE)).isFalse();
			assertThat(result.getReaction().get(HATE)).isFalse();

			assertThat(result.getProfile())
				.extracting(
					ProfileResult::getProfileId,
					ProfileResult::getUsername,
					ProfileResult::getImage,
					ProfileResult::isFollow
				).containsExactly(profile.getId(), profile.getUsername(), profile.getImage(), false);
		}

		@Test
		void 내가_좋아요_누른_북마크_상세_조회_성공() {
			//given
			reactionRepository.save(new Reaction(profile, bookmark, "like"));

			//when
			final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(userId, bookmark.getId());

			//then
			assertAll(
				() -> assertThat(result.getReactionCount())
					.containsExactlyInAnyOrderEntriesOf(Map.of(LIKE, 1L, HATE, 0L)),
				() -> assertThat(result.getReaction())
					.containsExactlyInAnyOrderEntriesOf(Map.of(LIKE, true, HATE, false))
			);
		}

		@Test
		void 내가_즐겨찾기한_북마크_상세_조회_성공() {
			//given
			favoriteRepository.save(new Favorite(bookmark, profile));

			//when
			final GetDetailedBookmarkResult result = bookmarkService.getDetailedBookmark(userId, bookmark.getId());

			//then
			assertThat(result.isFavorite()).isTrue();
		}

		@Test
		void 북마크_조회_실패_존재하지_않는_북마크() {
			//given
			final long invalidBookmarkId = 10L;

			//when then
			assertThatLinkoceanRuntimeException()
				.isThrownBy(() -> bookmarkService.getDetailedBookmark(userId, invalidBookmarkId));
		}

	}

	private RegisterBookmarkCommand command(long userId, final String url) {
		return new RegisterBookmarkCommand(userId, url, null, null, null, OpenType.ALL, emptyList());
	}

	@Nested
	class 다른_사람_북마크_목록_조회_테스트 {

		private Profile profile2;
		private long profileId2;

		private long bookmarkId1;
		private long bookmarkId2;
		private long bookmarkId3;

		@BeforeEach
		void setUp() {
			User user2 = userRepository.save(new User("crush@gmail.com", "GOOGLE"));
			profileId2 = profileRepository.save(new Profile(user2, "crush")).getId();
			linkMetadataRepository.save(new LinkMetadata("http://www.naver.com", "네이버", "naver.png"));
			linkMetadataRepository.save(new LinkMetadata("http://www.daum.com", "다음", "daum.png"));
			linkMetadataRepository.save(new LinkMetadata("http://www.kakao.com", "카카오", "kakao.png"));

			final RegisterBookmarkCommand command1 = new RegisterBookmarkCommand(
				user2.getId(),
				"http://www.naver.com",
				"title1",
				null,
				Category.IT,
				OpenType.ALL,
				List.of("tag1", "tag2"));
			bookmarkId1 = bookmarkService.registerBookmark(command1);

			final RegisterBookmarkCommand command2 = new RegisterBookmarkCommand(
				user2.getId(),
				"http://www.kakao.com",
				"title2",
				null,
				Category.IT,
				OpenType.PARTIAL,
				List.of("tag2"));
			bookmarkId2 = bookmarkService.registerBookmark(command2);

			final RegisterBookmarkCommand command3 = new RegisterBookmarkCommand(
				user2.getId(),
				"http://www.google.com",
				"title3",
				null,
				Category.HOME,
				OpenType.PRIVATE,
				List.of("tag1"));
			bookmarkId3 = bookmarkService.registerBookmark(command3);

			System.out.println("set up complete");
		}

		/* 다른 사람과 팔로우/팔로이 관계가 아니므로 공개 범위가 all 인 글만 볼 수 있다 */
		@Test
		void 다른_사람_북마크_목록_조회() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(userId)
				.writerProfileId(profileId2)
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByWriterProfileId(findCond, pageable);

			//then
			assertThat(resultPage.getContent()).hasSize(1)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::getOpenType)
				.containsExactly(tuple(bookmarkId1, OpenType.ALL));

		}

		/* 다른 사람과 팔로우/팔로이 관계기 때문에 공개 범위가 all, partial 인 글을 볼 수 있다 */
		@Disabled("이상함 나중에 고칠것")
		@Test
		void 팔로워_팔로이_관계인_사람의_북마크_목록_조회() {
			//given
			followRepository.save(new Follow(profile, profile2));
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(userId)
				.writerProfileId(profileId2)
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<GetBookmarksResult> resultPage = bookmarkService.getByWriterProfileId(findCond, pageable);

			//then
			assertThat(resultPage.getContent()).hasSize(2)
				.extracting(GetBookmarksResult::getId, GetBookmarksResult::getOpenType)
				.containsExactly(tuple(bookmarkId2, OpenType.PARTIAL), tuple(bookmarkId1, OpenType.ALL));
		}
	}

	@Test
	void 프로필_아이디_url_로_북마크_존재하는지_확인_성공() {
		//given
		final Bookmark bookmark = bookmarkRepository.save(createBookmark(profile, linkMetadata));

		//when
		final Optional<Long> duplicated = bookmarkService.getBookmarkIdIfExist(userId, bookmark.getUrl());
		final Optional<Long> notDuplicated = bookmarkService.getBookmarkIdIfExist(userId, "https://www.does.not.exist");

		//then
		assertThat(duplicated).isPresent().get().isEqualTo(bookmark.getId());
		assertThat(notDuplicated).isEmpty();
	}
}
