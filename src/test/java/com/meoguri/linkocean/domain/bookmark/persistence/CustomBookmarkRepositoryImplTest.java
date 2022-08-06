package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meoguri.linkocean.common.P6spyLogMessageFormatConfiguration;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.FindBookmarksDefaultCond;
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

	private Bookmark savedBookmark1;
	private Bookmark savedBookmark2;
	private Bookmark savedBookmark3;

	private long bookmarkId1;
	private long bookmarkId2;
	private long bookmarkId3;

	private static final Pageable pageable = Pageable.ofSize(8);

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
		savedBookmark1 = bookmarkRepository.save(bookmark1);

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
		savedBookmark2 = bookmarkRepository.save(bookmark2);

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
		savedBookmark3 = bookmarkRepository.save(bookmark3);

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
	}

	@Nested
	class 북마크_카테고리로_조회 {

		// TODO - 정렬 구현 후 InAnyOrder 때기
		@Test
		void 북마크_카테고리로_조회_성공() {
			//given
			final Category findCategory = Category.IT;
			final FindBookmarksDefaultCond findCond = cond();

			//given when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactlyInAnyOrder(
					tuple(bookmarkId3, Category.IT.getKorName()),
					tuple(bookmarkId1, Category.IT.getKorName())
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Disabled("정렬 구현 후 풀기")
		@Test
		void 북마크_카테고리로_조회_필터링_좋아요_정렬() {
			//given
			final Category findCategory = Category.IT;
			final FindBookmarksDefaultCond findCond = cond("like", null);

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, findCond, pageable);

			//then
			assertThat(bookmarks).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getCategory)
				.containsExactly(
					tuple(bookmarkId1, Category.IT.getKorName()),
					tuple(bookmarkId3, Category.IT.getKorName())
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		}

		@Test
		void 북마크_카테고리로_조회_제목으로_필터링() {
			//given
			final Category findCategory = Category.IT;
			final FindBookmarksDefaultCond cond = cond("1");

			//when
			final Page<Bookmark> bookmarks = bookmarkRepository.findByCategory(findCategory, cond, pageable);

			//then
			assertThat(bookmarks).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
				.containsExactly(
					tuple(bookmarkId1, Category.IT.getKorName(), "title1")
				);
			assertThat(bookmarks.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_즐겨찾기_조회 {

		//TODO - 정렬 구현 후 InAnyOrder 때기
		@Test
		void 북마크_즐겨찾기_조회_제목으로_필터링_성공() {
			//given
			final FindBookmarksDefaultCond cond = cond("1");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findFavoriteBookmarks(cond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId)
				.containsExactlyInAnyOrder(bookmarkId1);
			assertThat(bookmarkPage).allSatisfy(b -> favoriteRepository.existsByOwnerAndBookmark(profile, b));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1L);
		}

		@Disabled("정렬 구현 후 테스트")
		@Test
		void 북마크_즐겨찾기_조회_좋아요_정렬_성공() {
		/*//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByFavoriteAndDefaultCond(
			true,
			cond("like", profile.getId(), null),
			pageable);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId)
			.containsExactly(savedBookmark1.getId(), savedBookmark2.getId());*/
		}
	}

	@Nested
	class 북마크_태그로_조회 {

		//TODO - 정렬 구현 후 InAnyOrder 때기
		@Test
		void 북마크_태그로_조회_성공() {
			//given
			final List<String> searchTags = List.of("tag1");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, cond(), pageable);

			//then
			assertThat(bookmarkPage).hasSize(2)
				.extracting(Bookmark::getId, Bookmark::getTagNames)
				.containsExactlyInAnyOrder(
					tuple(bookmarkId2, List.of("tag1")),
					tuple(bookmarkId1, List.of("tag1", "tag2"))
				);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(2);
		}

		@Disabled("정렬 구현 후 풀기")
		@Test
		void 북마크_태그로_조회_좋아요_정렬_성공() {
			//given
			final List<String> searchTags = List.of("tag1");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, cond("like", null), pageable);

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

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findByTags(searchTags, cond("1"), pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTagNames, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, List.of("tag1", "tag2"), "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}
	}

	@Nested
	class 북마크_기본_조회 {

		//TODO - 정렬 구현 후 InAnyOrder 때기
		@Test
		void 북마크_기본_조회_성공() {
			//given
			final FindBookmarksDefaultCond findCond = cond("upload", null);

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactlyInAnyOrder(bookmarkId3, bookmarkId2, bookmarkId1);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Disabled
		@Test
		void 북마크_기본_조회_좋아요_정렬_성공() {
			//given
			final FindBookmarksDefaultCond findCond = cond("like", null);

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(3)
				.extracting(Bookmark::getId)
				.containsExactlyInAnyOrder(bookmarkId1, bookmarkId3, bookmarkId2);
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(3);
		}

		@Test
		void 북마크_기본_조회_제목으로_필터링() {
			//given
			final FindBookmarksDefaultCond findCond = cond("1");

			//when
			final Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarks(findCond, pageable);

			//then
			assertThat(bookmarkPage).hasSize(1)
				.extracting(Bookmark::getId, Bookmark::getTitle)
				.containsExactly(tuple(bookmarkId1, "title1"));
			assertThat(bookmarkPage.getTotalElements()).isEqualTo(1);
		}

	}

	private FindBookmarksDefaultCond cond(final String order, final String searchTitle) {
		return new FindBookmarksDefaultCond(profile.getId(), searchTitle, order);
	}

	private FindBookmarksDefaultCond cond(final String searchTitle) {
		return cond(null, searchTitle);
	}

	private FindBookmarksDefaultCond cond() {
		return cond(null);
	}
}
