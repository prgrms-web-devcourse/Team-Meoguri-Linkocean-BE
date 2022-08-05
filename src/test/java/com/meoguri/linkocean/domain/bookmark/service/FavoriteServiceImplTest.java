package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.service.LinkMetadataService;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

@Transactional
@SpringBootTest
class FavoriteServiceImplTest {

	@Autowired
	private FavoriteService favoriteService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private LinkMetadataService linkMetadataService;

	long userId;
	long profileId;
	long bookmarkId;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 북마크 셋업
		User user = userRepository.save(createUser());
		userId = user.getId();

		profileId = profileService.registerProfile(command(createProfile(user)));

		linkMetadataService.getTitleByLink("https://www.naver.com");
		bookmarkId = bookmarkService.registerBookmark(command(user, "www.naver.com"));
	}

	@Test
	void 즐겨찾기_추가() {
		//given
		favoriteService.favorite(userId, bookmarkId);

		//when
		final GetDetailedBookmarkResult detailedBookmark = bookmarkService.getDetailedBookmark(userId, bookmarkId);

		//then
		assertThat(detailedBookmark.isFavorite()).isTrue();
	}

	@Test
	void 즐겨찾기_해제() {

		//given
		favoriteService.favorite(userId, bookmarkId);

		//when
		favoriteService.unfavorite(userId, bookmarkId);
		final GetDetailedBookmarkResult detailedBookmark = bookmarkService.getDetailedBookmark(userId, bookmarkId);

		//then
		assertThat(detailedBookmark.isFavorite()).isFalse();
	}

	@Test
	void 즐겨찾기_추가_실패_이미_추가된_상태() {
		//given
		favoriteService.favorite(userId, bookmarkId);

		//when then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> favoriteService.favorite(userId, bookmarkId));
	}


	@Test
	void 즐겨찾기_해제_실패_즐겨찾기로_추가되어_있지_않음() {
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> favoriteService.unfavorite(userId, bookmarkId));
	}

	private RegisterBookmarkCommand command(final User user, final String url) {
		return new RegisterBookmarkCommand(user.getId(), url, null, null, null, "all", emptyList());
	}

	private RegisterProfileCommand command(Profile profile) {

		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			emptyList()
		);
	}
}
