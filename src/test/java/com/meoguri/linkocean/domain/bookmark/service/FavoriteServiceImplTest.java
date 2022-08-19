package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

@Import(
	{FavoriteServiceImpl.class,
		FindProfileByIdQuery.class,
		FindBookmarkByIdQuery.class,
	}
)
class FavoriteServiceImplTest extends BasePersistenceTest {

	@Autowired
	private FavoriteService favoriteService;

	// @Autowired
	// private BookmarkService bookmarkService;

	private Profile profile;
	private long profileId;

	private Bookmark bookmark;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		profile = 사용자_프로필_동시_저장_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);
		profileId = profile.getId();

		bookmark = 북마크_링크_메타데이터_동시_저장(profile, "google.com");
		bookmarkId = bookmark.getId();
	}

	@Test
	void 즐겨찾기_추가() {
		//when
		favoriteService.favorite(profileId, bookmarkId);

		//then
		// final GetDetailedBookmarkResult detailedBookmark = bookmarkService.getDetailedBookmark(profileId, bookmarkId);
		// assertThat(detailedBookmark.isFavorite()).isTrue();
	}

	@Test
	void 즐겨찾기_해제() {
		//given
		즐겨찾기_저장(profile, bookmark);

		//when
		favoriteService.unfavorite(profileId, bookmarkId);

		//then
		// final GetDetailedBookmarkResult detailedBookmark = bookmarkService.getDetailedBookmark(profileId, bookmarkId);
		// assertThat(detailedBookmark.isFavorite()).isFalse();
	}

	@Test
	void 즐겨찾기_추가_실패_이미_추가된_상태() {
		//given
		favoriteService.favorite(profileId, bookmarkId);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> favoriteService.favorite(profileId, bookmarkId));
	}

}
