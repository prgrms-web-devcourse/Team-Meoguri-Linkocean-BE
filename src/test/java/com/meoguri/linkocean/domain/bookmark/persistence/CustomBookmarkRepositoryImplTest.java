package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

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

	@BeforeEach
	void setUp() {
		final User user = userRepository.save(createUser("crush@mail.com", "NAVER"));
		profile = profileRepository.save(createProfile(user, "crush"));

		final LinkMetadata linkMetadata1 =
			linkMetadataRepository.save(new LinkMetadata("www.naver.com", "naver", "naver.png"));
		final LinkMetadata linkMetadata2 =
			linkMetadataRepository.save(new LinkMetadata("www.google.com", "google", "google.png"));
		final LinkMetadata linkMetadata3 =
			linkMetadataRepository.save(new LinkMetadata("www.github.com", "github", "github.png"));

		final Tag tag1 = tagRepository.save(new Tag("tag1"));
		final Tag tag2 = tagRepository.save(new Tag("tag2"));

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

		reactionRepository.save(new Reaction(profile, savedBookmark1, "like"));
		reactionRepository.save(new Reaction(profile, savedBookmark2, "hate"));

		favoriteRepository.save(new Favorite(savedBookmark1, profile));
		favoriteRepository.save(new Favorite(savedBookmark2, profile));

		em.flush();
		em.clear();
	}

	@Test
	void 내_북마크_카테고리로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndCategoryAndSearchTitle(profile, Category.IT, null);

		//then
		assertThat(totalCount).isEqualTo(2L);
	}

	@Test
	void 내_북마크_카테고리와_검색어로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndCategoryAndSearchTitle(profile, Category.IT, "1");

		//then
		assertThat(totalCount).isEqualTo(1L);
	}

	@Test
	void 내_북마크_조회_카테고리로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndCategoryAndDefaultCond(
			profile, Category.IT, new FindBookmarksDefaultCond(1, 8, "upload", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId, Bookmark::getCategory)
			.containsExactly(
				tuple(savedBookmark3.getId(), savedBookmark3.getCategory()),
				tuple(savedBookmark1.getId(), savedBookmark3.getCategory()));
	}

	@Disabled("북마크에 likeCount 추가 후 작업")
	@Test
	void 내_북마크_조회_카테고리_필터링_좋아요_정렬() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndCategoryAndDefaultCond(
			profile, Category.IT, new FindBookmarksDefaultCond(1, 8, "like", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId, Bookmark::getCategory)
			.containsExactly(
				tuple(savedBookmark1.getId(), savedBookmark1.getCategory()),
				tuple(savedBookmark3.getId(), savedBookmark3.getCategory()));
	}

	@Test
	void 내_북마크_조회_카테고리와_제목으로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndCategoryAndDefaultCond(
			profile, Category.IT, new FindBookmarksDefaultCond(1, 8, "upload", "1")
		);

		//then
		assertThat(bookmarks).hasSize(1)
			.extracting(Bookmark::getId, Bookmark::getCategory, Bookmark::getTitle)
			.containsExactly(tuple(savedBookmark1.getId(), savedBookmark1.getCategory(), savedBookmark1.getTitle()));
	}

	@Test
	void 내_북마크_조회_즐겨찾기로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndFavoriteAndSearchTitle(profile, true, null);

		//then
		assertThat(totalCount).isEqualTo(2L);
	}

	@Test
	void 내_북마크_조회_즐겨찾기와_제목으로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndFavoriteAndSearchTitle(profile, true, "1");

		//then
		assertThat(totalCount).isEqualTo(1L);
	}

	@Test
	void 내_북마크_조회_즐겨찾기로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndFavoriteAndDefaultCond(
			profile, true, new FindBookmarksDefaultCond(1, 8, "upload", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId)
			.containsExactly(savedBookmark2.getId(), savedBookmark1.getId());

		bookmarks.forEach(
			bookmark -> assertThat(favoriteRepository.existsByOwnerAndBookmark(profile, bookmark)).isTrue()
		);
	}

	@Disabled("북마크에 likeCount 추가 후 작업")
	@Test
	void 내_북마크_조회_즐겨찾기로_필터링_좋아요_정렬() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndFavoriteAndDefaultCond(
			profile, true, new FindBookmarksDefaultCond(1, 8, "like", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId)
			.containsExactly(savedBookmark1.getId(), savedBookmark2.getId());
	}

	@Test
	void 내_북마크_조회_즐겨찾기와_제목으로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndFavoriteAndDefaultCond(
			profile, true, new FindBookmarksDefaultCond(1, 8, "upload", "1")
		);

		//then
		assertThat(bookmarks).hasSize(1)
			.extracting(Bookmark::getId, Bookmark::getTitle)
			.containsExactly(tuple(savedBookmark1.getId(), savedBookmark1.getTitle()));
	}

	@Test
	void 내_북마크_조회_태그_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndTagsAndSearchTitle(profile, List.of("tag1"), null);

		//then
		assertThat(totalCount).isEqualTo(2L);
	}

	@Test
	void 내_북마크_조회_태그와_제목으로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndTagsAndSearchTitle(profile, List.of("tag1"), "1");

		//then
		assertThat(totalCount).isEqualTo(1L);
	}

	@Test
	void 내_북마크_조회_태그로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndTagsAndDefaultCond(
			profile, List.of("tag1"), new FindBookmarksDefaultCond(1, 8, "upload", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId, Bookmark::getTagNames)
			.containsExactly(
				tuple(savedBookmark2.getId(), savedBookmark2.getTagNames()),
				tuple(savedBookmark1.getId(), savedBookmark1.getTagNames()));
	}

	@Disabled("북마크에 likeCount 추가 후 작업")
	@Test
	void 내_북마크_조회_태그로_필터링_좋아요_정렬() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndTagsAndDefaultCond(
			profile, List.of("tag1"), new FindBookmarksDefaultCond(1, 8, "like", null)
		);

		//then
		assertThat(bookmarks).hasSize(2)
			.extracting(Bookmark::getId, Bookmark::getTagNames)
			.containsExactly(
				tuple(savedBookmark1.getId(), savedBookmark1.getTagNames()),
				tuple(savedBookmark2.getId(), savedBookmark2.getTagNames()));
	}

	@Test
	void 내_북마크_조회_태그와_제목으로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndTagsAndDefaultCond(
			profile, List.of("tag1"), new FindBookmarksDefaultCond(1, 8, "upload", "1")
		);

		//then
		assertThat(bookmarks).hasSize(1)
			.extracting(Bookmark::getId, Bookmark::getTagNames, Bookmark::getTitle)
			.containsExactly(tuple(savedBookmark1.getId(), savedBookmark1.getTagNames(), savedBookmark1.getTitle()));
	}

	@Test
	void 내_북마크_조회_총_개수() {
		//give when
		final long totalCount = bookmarkRepository.countByProfileAndSearchTitle(profile, null);

		//then
		assertThat(totalCount).isEqualTo(3L);
	}

	@Test
	void 내_북마크_조회_제목으로_필터링_총_개수() {
		//given when
		final long totalCount = bookmarkRepository.countByProfileAndSearchTitle(profile, "1");

		//then
		assertThat(totalCount).isEqualTo(1L);
	}

	@Test
	void 내_북마크_조회() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndDefaultCond(
			profile, new FindBookmarksDefaultCond(1, 8, "upload", null)
		);

		//then
		assertThat(bookmarks).hasSize(3)
			.extracting(Bookmark::getId)
			.containsExactly(savedBookmark3.getId(), savedBookmark2.getId(), savedBookmark1.getId());
	}

	@Disabled("북마크에 likeCount 추가 후 작업")
	@Test
	void 내_북마크_조회_좋아요_정렬() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndDefaultCond(
			profile, new FindBookmarksDefaultCond(1, 8, "like", null)
		);

		//then
		assertThat(bookmarks).hasSize(3)
			.extracting(Bookmark::getId)
			.containsExactly(savedBookmark1.getId(), savedBookmark3.getId(), savedBookmark2.getId());
	}

	@Test
	void 내_북마크_조회_제목으로_필터링() {
		//given when
		final List<Bookmark> bookmarks = bookmarkRepository.searchByProfileAndDefaultCond(
			profile, new FindBookmarksDefaultCond(1, 8, "upload", "1")
		);

		//then
		assertThat(bookmarks).hasSize(1)
			.extracting(Bookmark::getId, Bookmark::getTitle)
			.containsExactly(tuple(savedBookmark1.getId(), savedBookmark1.getTitle()));
	}
}
