package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
import com.meoguri.linkocean.domain.linkmetadata.service.LinkMetadataService;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

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
	void 즐겨찾기_추가_제거_성공() {
		//추가
		//when
		favoriteService.favorite(userId, bookmarkId);

		//then TODO - 북마크 서비스에서 조회 구현 이후 검증
		// final GetBookmarkResult result1 = bookmarkService.getBookmark(userId, bookmarkId);
		// assertThat(result1.isFavorite()).isTrue();

		//제거
		//when
		favoriteService.unfavorite(userId, bookmarkId);

		//then TODO - 북마크 서비스에서 조회 구현 이후 검증
		// final GetBookmarkResult result2 = bookmarkService.getBookmark(userId, bookmarkId);
		// assertThat(result2.isFavorite()).isFalse();
	}

	private RegisterBookmarkCommand command(final User user, final String url) {
		return new RegisterBookmarkCommand(user.getId(), url, null, null, null, emptyList(), "all");
	}

	private RegisterProfileCommand command(Profile profile) {

		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			emptyList()
		);
	}
}
