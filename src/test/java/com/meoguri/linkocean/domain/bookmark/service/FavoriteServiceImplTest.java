package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static java.util.Collections.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;
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

	User user;
	long userId;
	long profileId;
	long bookmarkId;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 북마크 셋업
		user = userRepository.save(createUser());
		profileId = profileService.registerProfile(registerCommandOf(createProfile(user)));
		bookmarkId = bookmarkService.registerBookmark(defaultBookmarkRegisterCommand(user));
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

	private RegisterBookmarkCommand defaultBookmarkRegisterCommand(final User user) {
		return new RegisterBookmarkCommand(user.getId(), "www.naver.com", null, null, null, emptyList(), "public");
	}

	public static RegisterProfileCommand registerCommandOf(Profile profile) {

		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			emptyList()
		);
	}
}
