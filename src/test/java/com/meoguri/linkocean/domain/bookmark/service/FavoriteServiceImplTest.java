package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class FavoriteServiceImplTest extends BaseServiceTest {

	@Autowired
	private FavoriteService favoriteService;

	private long profileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);
		bookmarkId = 북마크_링크_메타데이터_동시_등록(profileId, "www.google.com");
	}

	@Test
	void 즐겨찾기_추가() {
		//when
		favoriteService.favorite(profileId, bookmarkId);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
		assertThat(result.isFavorite()).isTrue();
	}

	@Test
	void 즐겨찾기_해제() {
		//given
		즐겨찾기(profileId, bookmarkId);

		//when
		favoriteService.unfavorite(profileId, bookmarkId);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId);
		assertThat(result.isFavorite()).isFalse();
	}

	@Test
	void 즐겨찾기_추가_실패_이미_추가된_상태() {
		//given
		즐겨찾기(profileId, bookmarkId);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> favoriteService.favorite(profileId, bookmarkId));
	}
}
