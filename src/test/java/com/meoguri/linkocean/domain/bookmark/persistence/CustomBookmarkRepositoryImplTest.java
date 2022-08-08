package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;
import com.meoguri.linkocean.common.Ultimate;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.UltimateBookmarkFindCond;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(P6spyLogMessageFormatConfiguration.class)
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

	private Profile profile;

	private long bookmarkId1;
	private long bookmarkId2;
	private long bookmarkId3;

	@BeforeEach
	void setUp() {
		// 사용자 1명 셋업 - 크러쉬
		final User user = userRepository.save(createUser("crush@mail.com", "NAVER"));
		profile = profileRepository.save(createProfile(user, "crush"));

		// 링크 메타 데이터 3개 셋업
		final LinkMetadata naver = new LinkMetadata("www.naver.com", "naver", "naver.png");
		final LinkMetadata google = new LinkMetadata("www.google.com", "google", "google.png");
		final LinkMetadata github = new LinkMetadata("www.github.com", "github", "github.png");

		final LinkMetadata linkMetadata1 = linkMetadataRepository.save(naver);
		final LinkMetadata linkMetadata2 = linkMetadataRepository.save(google);
		final LinkMetadata linkMetadata3 = linkMetadataRepository.save(github);

		// 태그 두개 셋업
		final Tag tag1 = tagRepository.save(new Tag("tag1"));
		final Tag tag2 = tagRepository.save(new Tag("tag2"));

		// 크러쉬가 북마크 1개 저장 - 네이버, IT, 전체 공개, #tag1, #tag2
		final Bookmark bookmark1 = builder()
			.profile(profile)
			.linkMetadata(linkMetadata1)
			.title("title1")
			.memo("memo1")
			.category("IT")
			.openType("all")
			.url("www.naver.com")
			.build();
		bookmark1.addBookmarkTag(tag1);
		bookmark1.addBookmarkTag(tag2);
		final Bookmark savedBookmark1 = bookmarkRepository.save(bookmark1);

		// 크러쉬가 북마크 2개 저장 - 구글, 가정, 일부 공개, #tag1
		final Bookmark bookmark2 = builder()
			.profile(profile)
			.linkMetadata(linkMetadata2)
			.title("title2")
			.memo("memo2")
			.category("가정")
			.openType("partial")
			.url("www.google.com")
			.build();
		bookmark2.addBookmarkTag(tag1);
		final Bookmark savedBookmark2 = bookmarkRepository.save(bookmark2);

		// 크러쉬가 북마크 3개 저장 - 깃헙, IT, 비공개, 태그 없음
		final Bookmark bookmark3 = builder()
			.profile(profile)
			.linkMetadata(linkMetadata3)
			.title("title3")
			.memo("memo3")
			.category("IT")
			.openType("private")
			.url("www.github.com")
			.build();
		final Bookmark savedBookmark3 = bookmarkRepository.save(bookmark3);

		// 크러쉬가 네이버에 좋아요를 누름
		reactionRepository.save(new Reaction(profile, savedBookmark1, "like"));
		bookmark1.changeLikeCount(1L);

		// 크러쉬가 구글에 싫어요를 누름
		reactionRepository.save(new Reaction(profile, savedBookmark2, "hate"));

		// 크러쉬가 네이버와 구글에 즐겨찾기를 누름
		favoriteRepository.save(new Favorite(savedBookmark1, profile));
		favoriteRepository.save(new Favorite(savedBookmark2, profile));

		em.flush();
		em.clear();

		bookmarkId1 = savedBookmark1.getId();
		bookmarkId2 = savedBookmark2.getId();
		bookmarkId3 = savedBookmark3.getId();

		System.out.println("set up complete");
	}

	@Nested
	class 북마크_카테고리로_조회 {

		@Test
		void 북마크_카테고리로_조회_성공() {
			//given
			final Category findCategory = Category.IT;
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId3, "IT"),
					tuple(bookmarkId1, "IT")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Ultimate
		@Test
		void name() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCond(Category.IT);
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId3, "IT"),
					tuple(bookmarkId1, "IT")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_필터링_좋아요_정렬() {
			//given
			final Category findCategory = Category.IT;
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId1, "IT"),
					tuple(bookmarkId3, "IT")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void name1() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCond(Category.IT);
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId1, "IT"),
					tuple(bookmarkId3, "IT")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_제목으로_필터링() {
			//given
			final Category findCategory = Category.IT;
			final BookmarkFindCond cond = cond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, cond, pageable);

			//then
			assertThat(bookmarks).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
				.containsExactly(
					tuple(bookmarkId1, "IT", "title1")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(1);
		}

		@Ultimate
		@Test
		void name2() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCond(Category.IT, "1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
				.containsExactly(
					tuple(bookmarkId1, "IT", "title1")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_즐겨찾기_조회 {

		@Test
		void 북마크_즐겨찾기_조회_제목으로_필터링_성공() {
			//given
			final BookmarkFindCond cond = cond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findFavoriteBookmarks(cond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1);
			assertThat(bookmarkPage).allSatisfy(b -> favoriteRepository.existsByOwnerAndBookmark(profile, b));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1L);
		}

		@Test
		void 북마크_즐겨찾기_조회_좋아요_순으로_정렬_성공() {
			//given
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = likePageable();

			// when
			final Page<Bookmark> bookmarks = bookmarkRepository.findFavoriteBookmarks(findCond, pageable);

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
			final List<String> searchTags = List.of("tag1");
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, findCond, pageable);

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
			final List<String> searchTags = List.of("tag1");
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, findCond, pageable);

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
			final List<String> searchTags = List.of("tag1");
			final BookmarkFindCond findCond = cond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, findCond, pageable);

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
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId3, bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_좋아요_정렬_성공() {
			//given
			final BookmarkFindCond findCond = cond();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId3, bookmarkId2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_제목으로_필터링() {
			//given
			final BookmarkFindCond findCond = cond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}

	}

	private BookmarkFindCond cond(final String searchTitle) {
		return new BookmarkFindCond(profile.getId(), searchTitle);
	}

	private BookmarkFindCond cond() {
		return cond(null);
	}

	private UltimateBookmarkFindCond ultimateCond(final Category category, final boolean favorite,
		final List<String> tags, final boolean follow, final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), category, favorite, tags, follow, title);
	}

	private UltimateBookmarkFindCond ultimateCond(final Category category) {
		return new UltimateBookmarkFindCond(0, profile.getId(), category, false, null, false, null);
	}

	private UltimateBookmarkFindCond ultimateCond(final Category category, final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), category, false, null, false, title);
	}

	private PageRequest defaultPageable() {
		return PageRequest.of(0, 8, Sort.by("upload"));
	}

	private PageRequest likePageable() {
		return PageRequest.of(0, 8, Sort.by("like", "upload"));
	}

}
