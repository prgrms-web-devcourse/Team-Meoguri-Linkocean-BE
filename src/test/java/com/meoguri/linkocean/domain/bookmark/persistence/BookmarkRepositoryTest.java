package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.common.CustomP6spySqlFormat;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(CustomP6spySqlFormat.class)
@DataJpaTest
class BookmarkRepositoryTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 프로필_url_이용한_북마크_조회() {
		//given
		final Bookmark bookmark = createBookmark();

		userRepository.save(bookmark.getProfile().getUser());
		profileRepository.save(bookmark.getProfile());
		linkMetadataRepository.save(bookmark.getLinkMetadata());

		bookmarkRepository.save(bookmark);

		//when
		final Optional<Bookmark> retrievedBookmark = bookmarkRepository.findByProfileAndLinkMetadata(
			bookmark.getProfile(),
			bookmark.getLinkMetadata());

		//then
		assertThat(retrievedBookmark).isNotNull();
	}
}
