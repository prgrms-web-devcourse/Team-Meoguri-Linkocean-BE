package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class CustomBookmarkRepositoryImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private ReactionRepository reactionRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@PersistenceContext
	private EntityManager em;

	private long profileId;
	private long bookmarkId1;
	private long bookmarkId2;
	private long bookmarkId3;

	private LinkMetadata naver;
	private LinkMetadata google;
	private LinkMetadata github;

	@BeforeEach
	void setUp() {
		// 사용자 1명 셋업 - 크러쉬
		final User user = userRepository.save(createUser("crush@mail.com", "NAVER"));
		Profile profile = profileRepository.save(createProfile(user, "crush"));
		profileId = profile.getId();

		// 링크 메타 데이터 3개 셋업
		naver = new LinkMetadata("www.naver.com", "naver", "naver.png");
		google = new LinkMetadata("www.google.com", "google", "google.png");
		github = new LinkMetadata("www.github.com", "github", "github.png");

		naver = linkMetadataRepository.save(naver);
		google = linkMetadataRepository.save(google);
		github = linkMetadataRepository.save(github);

		// 태그 두개 셋업
		final Tag tag1 = tagRepository.save(new Tag("tag1"));
		final Tag tag2 = tagRepository.save(new Tag("tag2"));

		// 크러쉬가 북마크 1개 저장 - 네이버, IT, 전체 공개, #tag1, #tag2
		final Bookmark bookmark1 = bookmarkRepository.save(new Bookmark(
			profile,
			naver,
			"title1",
			"memo1",
			ALL,
			Category.IT,
			"www.naver.com",
			List.of(tag1, tag2)
		));

		// 크러쉬가 북마크 2개 저장 - 구글, 가정, 일부 공개, #tag1
		final Bookmark bookmark2 = bookmarkRepository.save(new Bookmark(
			profile,
			google,
			"title2",
			"memo2",
			OpenType.PARTIAL,
			Category.HOME,
			"www.google.com",
			List.of(tag1)
		));

		// 크러쉬가 북마크 3개 저장 - 깃헙, IT, 비공개, 태그 없음
		final Bookmark bookmark3 = bookmarkRepository.save(new Bookmark(
			profile,
			github,
			"title3",
			"memo3",
			OpenType.PRIVATE,
			Category.IT,
			"www.github.com",
			emptyList()
		));

		// 크러쉬가 네이버에 좋아요를 누름
		reactionRepository.save(new Reaction(profile, bookmark1, "like"));
		// 크러쉬가 구글에 싫어요를 누름
		reactionRepository.save(new Reaction(profile, bookmark2, "hate"));

		// 크러쉬가 네이버와 구글에 즐겨찾기를 누름
		bookmark1.changeLikeCount(1L);
		favoriteRepository.save(new Favorite(bookmark1, profile));
		favoriteRepository.save(new Favorite(bookmark2, profile));

		em.flush();
		em.clear();

		bookmarkId1 = bookmark1.getId();
		bookmarkId2 = bookmark2.getId();
		bookmarkId3 = bookmark3.getId();

		System.out.println("set up complete");
	}

	@Nested
	class 북마크_카테고리로_조회 {

		@Test
		void 북마크_카테고리로_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.category(Category.IT)
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId3, Category.IT),
					tuple(bookmarkId1, Category.IT)
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_필터링_좋아요_정렬() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.category(Category.IT)
				.build();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId1, Category.IT),
					tuple(bookmarkId3, Category.IT)
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_제목으로_필터링() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.category(Category.IT)
				.title("1")
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
				.containsExactly(
					tuple(bookmarkId1, Category.IT, "title1")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_즐겨찾기_조회 {

		@Test
		void 북마크_즐겨찾기_조회_제목으로_필터링_성공() {
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.title("1")
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1);
			assertThat(bookmarkPage).allSatisfy(b -> favoriteRepository.existsByOwner_idAndBookmark(profileId, b));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1L);
		}

		@Test
		void 북마크_즐겨찾기_조회_좋아요_순으로_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.favorite(true)
				.build();
			final Pageable pageable = likePageable();

			// when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId2);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}
	}

	@Nested
	class 북마크_태그로_조회 {

		@Test
		void 북마크_태그로_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.tags(List.of("tag1"))
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getTagNames)
				.containsExactly(
					tuple(bookmarkId2, List.of("tag1")),
					tuple(bookmarkId1, List.of("tag1", "tag2"))
				);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_태그로_조회_좋아요_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.tags(List.of("tag1"))
				.build();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getTagNames)
				.containsExactly(
					tuple(bookmarkId1, List.of("tag1", "tag2")),
					tuple(bookmarkId2, List.of("tag1"))
				);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_태그로_조회_제목_필터링_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.tags(List.of("tag1"))
				.title("1")
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTagNames, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, List.of("tag1", "tag2"), "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_기본_조회 {

		@Test
		void 북마크_기본_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId3, bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_Partial_공개범위_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.openType(OpenType.PARTIAL)
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_기본_조회_좋아요_정렬_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.build();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId3, bookmarkId2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_제목으로_필터링() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.writerProfileId(profileId)
				.title("1")
				.build();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByWriterId(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 피드_북마크_조회 {

		@Autowired
		private FollowRepository followRepository;

		private long profileId1;

		private Bookmark bookmark4;
		private Bookmark bookmark5;
		private Bookmark bookmark6;

		private Bookmark bookmark7;
		private Bookmark bookmark8;

		private Bookmark bookmark10;

		//  사용자 1 			-팔로우->	사용자 2 				사용자 3
		// bookmark4 all,    		bookmark7 all, 		bookmark10 all
		// bookmark5 partial,		bookmark8 partial	bookmark11 partial
		// bookmark6 private,       bookmark9 private   bookmark12 private
		@BeforeEach
		void setUp() {
			reactionRepository.deleteAll();
			favoriteRepository.deleteAll();
			bookmarkRepository.deleteAll(); // clean data by crush @ above setUp method

			final User user1 = userRepository.save(new User("user1@gmail.com", "GOOGLE"));
			final User user2 = userRepository.save(new User("user2@gmail.com", "GOOGLE"));
			final User user3 = userRepository.save(new User("user3@gmail.com", "GOOGLE"));

			final Profile profile1 = profileRepository.save(new Profile(user1, "user1"));
			final Profile profile2 = profileRepository.save(new Profile(user2, "user2"));
			final Profile profile3 = profileRepository.save(new Profile(user3, "user3"));
			profileId1 = profile1.getId();

			bookmarkRepository.save(createBookmark(profile3, github, PRIVATE, "github.com"));
			bookmarkRepository.save(createBookmark(profile3, google, PARTIAL, "google.com"));
			bookmark10 = bookmarkRepository.save(createBookmark(profile3, naver, ALL, "naver.com"));

			bookmarkRepository.save(createBookmark(profile2, github, PRIVATE, "github.com"));
			bookmark8 = bookmarkRepository.save(createBookmark(profile2, google, PARTIAL, "google.com"));
			bookmark7 = bookmarkRepository.save(createBookmark(profile2, naver, ALL, "naver.com"));

			bookmark6 = bookmarkRepository.save(createBookmark(profile1, github, PRIVATE, "github.com"));
			bookmark5 = bookmarkRepository.save(createBookmark(profile1, google, PARTIAL, "google.com"));
			bookmark4 = bookmarkRepository.save(createBookmark(profile1, naver, ALL, "naver.com"));

			followRepository.save(new Follow(profile1, profile2));
		}

		@Test
		void 피드_북마크_조회_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.build();
			final PageRequest pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(6);
			assertThat(bookmarkPage.getContent())
				.containsExactly(bookmark4, bookmark5, bookmark6, bookmark7, bookmark8, bookmark10);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(6);
		}

		@Test
		void 피드_북마크_조회_팔로우_여부로_성공() {
			//given
			final BookmarkFindCond findCond = BookmarkFindCond.builder()
				.currentUserProfileId(profileId1)
				.follow(true)
				.build();
			final PageRequest pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2);
			assertThat(bookmarkPage.getContent())
				.containsExactly(bookmark7, bookmark8);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}
	}
}
