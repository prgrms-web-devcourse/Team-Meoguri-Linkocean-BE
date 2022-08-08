package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
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
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
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
			.tags(List.of(tag1, tag2))
			.build();
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
			.tags(List.of(tag1))
			.build();
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
			.tags(Collections.emptyList())
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

	@Ultimate
	@Nested
	class 북마크_카테고리로_조회 {

		@Ultimate
		@Test
		void 북마크_카테고리로_조회_성공() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCategoryFindCond(Category.IT);
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

		@Ultimate
		@Test
		void 북마크_카테고리로_조회_필터링_좋아요_정렬() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCategoryFindCond(Category.IT);
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

		@Ultimate
		@Test
		void 북마크_카테고리로_조회_제목으로_필터링() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateCategoryFindCond(Category.IT, "1");
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

	@Ultimate
	@Nested
	class 북마크_즐겨찾기_조회 {

		@Ultimate
		@Test
		void 북마크_즐겨찾기_조회_제목으로_필터링_성공() {
			final UltimateBookmarkFindCond findCond = ultimateFavoriteFindCond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1);
			assertThat(bookmarkPage).allSatisfy(b -> favoriteRepository.existsByOwnerAndBookmark(profile, b));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1L);
		}

		@Ultimate
		@Test
		void 북마크_즐겨찾기_조회_좋아요_순으로_정렬_성공() {
			final UltimateBookmarkFindCond findCond = ultimateFavoriteFindCond();
			final Pageable pageable = likePageable();

			// when
			final Page<Bookmark> bookmarks = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId2);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}
	}

	@Ultimate
	@Nested
	class 북마크_태그로_조회 {

		@Ultimate
		@Test
		void 북마크_태그로_조회_성공() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateTagFindCond(List.of("tag1"));
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getTagNames)
				.containsExactly(
					tuple(bookmarkId2, List.of("tag1")),
					tuple(bookmarkId1, List.of("tag1", "tag2"))
				);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Ultimate
		@Test
		void 북마크_태그로_조회_좋아요_정렬_성공() {

			//given
			final UltimateBookmarkFindCond findCond = ultimateTagFindCond(List.of("tag1"));
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getTagNames)
				.containsExactly(
					tuple(bookmarkId1, List.of("tag1", "tag2")),
					tuple(bookmarkId2, List.of("tag1"))
				);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Ultimate
		@Test
		void 북마크_태그로_조회_제목_필터링_성공() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateTagFindCond(List.of("tag1"), "1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTagNames, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, List.of("tag1", "tag2"), "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}
	}

	@Ultimate
	@Nested
	class 북마크_기본_조회 {

		@Ultimate
		@Test
		void 북마크_기본_조회_성공() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateFindCond();
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId3, bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Ultimate
		@Test
		void 북마크_기본_조회_좋아요_정렬_성공() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateFindCond();
			final Pageable pageable = likePageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactly(bookmarkId1, bookmarkId3, bookmarkId2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Ultimate
		@Test
		void 북마크_기본_조회_제목으로_필터링() {
			//given
			final UltimateBookmarkFindCond findCond = ultimateFindCond("1");
			final Pageable pageable = defaultPageable();

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.ultimateFindBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}
	}

	// 검색 조건, 페이지 픽스쳐
	private UltimateBookmarkFindCond ultimateCategoryFindCond(final Category category) {
		return ultimateCategoryFindCond(category, null);
	}

	private UltimateBookmarkFindCond ultimateCategoryFindCond(final Category category, final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), category, false, null, false, title);
	}

	private UltimateBookmarkFindCond ultimateFavoriteFindCond(final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), null, true, null, false, title);
	}

	private UltimateBookmarkFindCond ultimateFavoriteFindCond() {
		return ultimateFavoriteFindCond(null);
	}

	private UltimateBookmarkFindCond ultimateTagFindCond(final List<String> tags, final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), null, false, tags, false, title);
	}

	private UltimateBookmarkFindCond ultimateTagFindCond(final List<String> tags) {
		return ultimateTagFindCond(tags, null);
	}

	private UltimateBookmarkFindCond ultimateFindCond() {
		return ultimateFindCond(null);
	}

	private UltimateBookmarkFindCond ultimateFindCond(final String title) {
		return new UltimateBookmarkFindCond(0, profile.getId(), null, false, null, false, title);
	}

	private PageRequest defaultPageable() {
		return PageRequest.of(0, 8, Sort.by("upload"));
	}

	private PageRequest likePageable() {
		return PageRequest.of(0, 8, Sort.by("like", "upload"));
	}

}
