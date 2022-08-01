package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class FavoriteRepositoryTest {

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	private Bookmark bookmark;
	private Profile owner;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 북마크 셋업
		final User user = userRepository.save(createUser());
		owner = profileRepository.save(createProfile(user));

		final LinkMetadata linkMetadata = linkMetadataRepository.save(createLinkMetadata());
		bookmark = bookmarkRepository.save(createBookmark(owner, linkMetadata));
	}

	@Test
	void 페이보릿_저장_삭제_성공() {
		// 저장
		favoriteRepository.save(new Favorite(bookmark, owner));
		assertThat(favoriteRepository.findAll()).hasSize(1);

		// 삭제
		final int deletedCount = favoriteRepository.deleteByOwnerAndBookmark(owner, bookmark);
		assertThat(deletedCount).isEqualTo(1);
		assertThat(favoriteRepository.findAll()).isEmpty();
	}

	@Test
	void 즐겨찾기_여부_조회() {
		//when
		final boolean isFavorite = favoriteRepository.existsByOwnerAndBookmark(owner, bookmark);

		//then
		assertThat(isFavorite).isFalse();
	}
}
