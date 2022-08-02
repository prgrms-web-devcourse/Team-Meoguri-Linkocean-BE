package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkQueryDto;
import com.meoguri.linkocean.domain.bookmark.service.dto.MyBookmarkSearchCond;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
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
			.build();
		savedBookmark3 = bookmarkRepository.save(bookmark3);

		reactionRepository.save(new Reaction(profile, savedBookmark1, "like"));
		reactionRepository.save(new Reaction(profile, savedBookmark2, "hate"));
	}

	@Test
	void 내_북마크_조회_조건_없음() {
		//given
		final MyBookmarkSearchCond cond = cond(null, null, null, null);

		em.flush();
		em.clear();

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(3)
			.extracting(BookmarkQueryDto::getId, bookmarkQueryDto -> bookmarkQueryDto.getTagNames().size())
			.containsExactly(
				new Tuple(savedBookmark3.getId(), 0),
				new Tuple(savedBookmark2.getId(), 1),
				new Tuple(savedBookmark1.getId(), 2)
			);
	}

	@Test
	void 내_북마크_조회_좋아요_순으로_정렬() {
		//given
		final MyBookmarkSearchCond cond = cond("like", null, null, null);

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(3)
			.extracting(BookmarkQueryDto::getLikeCount, BookmarkQueryDto::getCategory)
			.containsExactly(
				new Tuple(1L, "IT"),
				new Tuple(0L, "IT"),
				new Tuple(0L, "가정"));
	}

	@Test
	void 내_북마크_조회_카테고리_필터링() {
		//given
		final MyBookmarkSearchCond cond = cond(null, "IT", null, null);

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).extracting(
			BookmarkQueryDto::getId,
			BookmarkQueryDto::getUrl,
			BookmarkQueryDto::getTitle,
			BookmarkQueryDto::getOpenType,
			BookmarkQueryDto::getCategory,
			BookmarkQueryDto::getUpdatedAt,
			BookmarkQueryDto::isFavorite,
			BookmarkQueryDto::getLikeCount,
			BookmarkQueryDto::getImageUrl,
			BookmarkQueryDto::getTagNames
		).containsExactly(
			savedBookmark3.getId(),
			savedBookmark3.getLinkMetadata().getSavedUrl(),
			savedBookmark3.getTitle(),
			savedBookmark3.getOpenType(),
			savedBookmark3.getCategory(),
			savedBookmark3.getUpdatedAt(),
			false,
			0L,
			savedBookmark3.getLinkMetadata().getImageUrl(),
			savedBookmark3.getTagNames()
		);
	}

	@Test
	void 내_북마크_조회_제목_필터링() {
		//given
		final MyBookmarkSearchCond cond = cond(null, null, "1", null);

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.get(0))
			.extracting(BookmarkQueryDto::getTitle, BookmarkQueryDto::getTagNames)
			.containsExactly(savedBookmark1.getTitle(), savedBookmark1.getTagNames());
	}

	@Test
	void 내_북마크_조회_테그_필터링_1() {
		//given
		final MyBookmarkSearchCond cond = cond(null, null, null, List.of("tag2"));

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.get(0))
			.extracting(BookmarkQueryDto::getId, BookmarkQueryDto::getTagNames)
			.containsExactly(savedBookmark1.getId(), savedBookmark1.getTagNames());
	}

	@Test
	void 내_북마크_조회_테그_필터링_2() {
		//given
		final MyBookmarkSearchCond cond = cond(null, null, null, List.of("tag1", "tag2"));

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(2)
			.extracting(BookmarkQueryDto::getId, BookmarkQueryDto::getTagNames)
			.containsExactly(
				new Tuple(savedBookmark2.getId(), savedBookmark2.getTagNames()),
				new Tuple(savedBookmark1.getId(), savedBookmark1.getTagNames())
			);
	}

	@Test
	void 내_북마크_조회_카테고리_검색어_필터링() {
		//given
		final MyBookmarkSearchCond cond = cond(null, "IT", "2", null);

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).isEmpty();
	}

	@Test
	void 내_북마크_조회_검색어_태그_좋아요순_정렬() {
		//given
		final MyBookmarkSearchCond cond = cond("like", null, "title", List.of("tag1"));

		//when
		final List<BookmarkQueryDto> result = bookmarkRepository.findMyBookmarksUsingSearchCond(profile, cond);

		//then
		assertThat(result).hasSize(2).extracting(
			BookmarkQueryDto::getId,
			BookmarkQueryDto::getTitle,
			BookmarkQueryDto::getLikeCount,
			BookmarkQueryDto::getTagNames
		).containsExactly(
			new Tuple(savedBookmark1.getId(), savedBookmark1.getTitle(), 1L, savedBookmark1.getTagNames()),
			new Tuple(savedBookmark2.getId(), savedBookmark2.getTitle(), 0L, savedBookmark2.getTagNames())
		);
	}

	private MyBookmarkSearchCond cond(final String order, final String category, final String searchTitle,
		final List<String> tags) {
		return new MyBookmarkSearchCond(null, null, order, category, searchTitle, tags);
	}
}
