package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.meoguri.linkocean.common.CustomP6spySqlFormat;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@TestPropertySource(properties = {
	"logging.level.org.hibernate.SQL=DEBUG",
	"logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE"
})
@Import(CustomP6spySqlFormat.class)
@DataJpaTest
class BookmarkRepositoryTest {

	@PersistenceContext
	private EntityManager em;

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
	void 사용자의_전체_북마크조회_태그_까지_페치_성공() {
		//given
		final Bookmark bookmark1 = createBookmark(profile, link, "bookmark1");
		final Bookmark bookmark2 = createBookmark(profile, link, "bookmark2");
		final Bookmark bookmark3 = createBookmark(profile, link, "bookmark3");

		bookmark1.addBookmarkTag(tag1);
		bookmark1.addBookmarkTag(tag2);
		bookmark1.addBookmarkTag(tag3);

		bookmark2.addBookmarkTag(tag2);
		bookmark2.addBookmarkTag(tag3);

		bookmark3.addBookmarkTag(tag3);

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

}
