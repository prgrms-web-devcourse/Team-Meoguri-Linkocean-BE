package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.common.CustomP6spySqlFormat;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(CustomP6spySqlFormat.class)
@DataJpaTest
class BookmarkRepositoryTest {

	@PersistenceContext
	private EntityManager em;

	@PersistenceUnit
	private EntityManagerFactory emf;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;

	private Profile profile;
	private LinkMetadata link;
	private Tag tag1;
	private Tag tag2;
	private Tag tag3;

	@BeforeEach
	void setUp() {
		// 프로필, 링크 셋업
		User user = userRepository.save(createUser());
		profile = profileRepository.save(createProfile(user));
		link = linkMetadataRepository.save(createLinkMetadata());

		// 태그 셋업
		tag1 = tagRepository.save(new Tag("tag1"));
		tag2 = tagRepository.save(new Tag("tag2"));
		tag3 = tagRepository.save(new Tag("tag3"));
	}

	@Test
	void 프로필_url_이용한_북마크_조회() {
		//given
		final Bookmark bookmark = createBookmark(profile, link);
		bookmarkRepository.save(bookmark);

		//when
		final Optional<Bookmark> oBookmark =
			bookmarkRepository.findByProfileAndLinkMetadata(bookmark.getProfile(), bookmark.getLinkMetadata());

		//then
		assertThat(oBookmark).isPresent();
	}

	@Test
	void 프로필_북마크_아이디_이용한_북마크_조회() {
		//given
		final Bookmark bookmark = createBookmark(profile, link);
		final Bookmark savedBookmark = bookmarkRepository.save(bookmark);

		//when
		final Optional<Bookmark> oBookmark =
			bookmarkRepository.findByProfileIdAndId(profile.getId(), bookmark.getId());

		//then
		assertThat(oBookmark).isPresent().get()
			.isEqualTo(savedBookmark);
	}

	@Test
	void 사용자의_전체_북마크조회_태그_까지_페치_성공() {
		//given
		final Bookmark bookmark1 = createBookmark(profile, link, "bookmark1", Category.IT, "www.naver.com",
			List.of(tag1, tag2, tag3));
		final Bookmark bookmark2 = createBookmark(profile, link, "bookmark2", Category.IT, "www.google.com",
			List.of(tag2, tag3));
		final Bookmark bookmark3 = createBookmark(profile, link, "bookmark3", Category.IT, "www.haha.com",
			List.of(tag3));

		bookmarkRepository.save(bookmark1);
		bookmarkRepository.save(bookmark2);
		bookmarkRepository.save(bookmark3);

		em.flush();
		em.clear();

		//when
		final List<Bookmark> bookmarks = bookmarkRepository.findByProfileFetchTags(profile);

		em.flush();
		em.clear();

		//then
		assertThat(bookmarks).hasSize(3)
			.extracting(Bookmark::getTitle, Bookmark::getTagNames)
			.containsExactly(
				tuple("bookmark1", List.of("tag1", "tag2", "tag3")),
				tuple("bookmark2", List.of("tag2", "tag3")),
				tuple("bookmark3", List.of("tag3"))
			);
	}

	@Disabled
	@Test
	void 북마크와_연관관계_맺은_엔티티_페치_조인_이용해_같이_조회() {
		//given
		final Bookmark bookmark = bookmarkRepository.save(createBookmark(profile, link));

		em.flush();
		em.clear();

		//when
		final Optional<Bookmark> oRetrievedBookmark = bookmarkRepository
			.findByIdFetchProfileAndLinkMetadataAndTags(bookmark.getId());

		//then
		assertAll(
			() -> assertThat(oRetrievedBookmark).isPresent(),
			() -> assertThat(isLoaded(oRetrievedBookmark.get().getProfile())).isTrue(),
			() -> assertThat(isLoaded(oRetrievedBookmark.get().getLinkMetadata())).isTrue(),
			() -> assertThat(oRetrievedBookmark.get().getTagNames()).contains(tag1.getName())
		);
	}

	private boolean isLoaded(final Object entity) {
		return emf.getPersistenceUnitUtil().isLoaded(entity);
	}

	@Test
	void 게시글이_존재하는_카테고리이름_반환() {
		//given
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.google.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.naver.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "www.prgrms.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.SOCIAL, "www.daum.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.SOCIAL, "www.hello.com"));
		bookmarkRepository.save(createBookmark(profile, link, "제목", Category.SCIENCE, "www.linkocean.com"));

		//when
		final List<String> categories = bookmarkRepository.findCategoryExistsBookmark(profile);

		//then
		assertThat(categories).contains("IT", "SOCIAL", "SCIENCE");
	}

	@Test
	void 프로필_아이디_url_로_북마크_존재하는지_확인_성공() {
		//given
		final Bookmark bookmark =
			bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "https://www.google.com"));

		//when
		final Optional<Long> oBookmarkId1 =
			bookmarkRepository.findBookmarkIdByProfileIdAndUrl(profile.getId(), "https://www.google.com");
		final Optional<Long> oBookmarkId2 =
			bookmarkRepository.findBookmarkIdByProfileIdAndUrl(profile.getId(), "https://www.does.not.exist");

		//then
		assertThat(oBookmarkId1).isPresent().get().isEqualTo(bookmark.getId());
		assertThat(oBookmarkId2).isEmpty();
	}

	@Test
	void 북마크_LikeCount_증가_성공() {
		//given
		final Bookmark bookmark =
			bookmarkRepository.save(createBookmark(profile, link, "제목", Category.IT, "https://www.google.com"));

		//when
		bookmarkRepository.addBookmarkLikeCount(bookmark.getId(), 1L);

		//then
		assertThat(bookmarkRepository.findById(bookmark.getId()).get().getLikeCount()).isOne();
	}
}
