package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

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
	private LinkMetadata linkMetadata;
	private Profile owner;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 북마크 셋업
		final User user = userRepository.save(createUser());
		owner = profileRepository.save(createProfile(user));

		linkMetadata = linkMetadataRepository.save(createLinkMetadata());
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

	@Test
	void 여러_북마크에_대해_즐겨찾기_PK를_조회() {
		//given
		final User user = userRepository.save(createUser("crush@mail.com", "GOOGLE"));
		final Profile profile = profileRepository.save(createProfile(user, "crush"));
		final Bookmark bookmark = bookmarkRepository.save(createBookmark(profile, linkMetadata));

		final Favorite favorite = favoriteRepository.save(new Favorite(this.bookmark, owner));

		//when
		final Set<Long> favoriteIds = favoriteRepository.findByOwnerAndBookmark(owner,
			List.of(this.bookmark, bookmark));

		//then
		assertThat(favoriteIds).hasSize(1)
			.containsExactly(favorite.getId());
	}
}
