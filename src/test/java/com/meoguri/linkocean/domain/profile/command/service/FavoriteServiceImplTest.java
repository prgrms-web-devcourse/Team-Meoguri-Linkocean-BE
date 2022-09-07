package com.meoguri.linkocean.domain.profile.command.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetDetailedBookmarkResult;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

class FavoriteServiceImplTest extends BaseServiceTest {

	@Autowired
	private FavoriteService favoriteService;

	private Logger log = LoggerFactory.getLogger(FavoriteServiceImplTest.class);

	private long profileId;
	private long bookmarkId1;
	private long bookmarkId2;

	@BeforeEach
	void setUp() {
		log.info("== set up start ==");

		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);
		bookmarkId1 = 북마크_링크_메타데이터_동시_등록(profileId, "www.google.com");
		bookmarkId2 = 북마크_링크_메타데이터_동시_등록(profileId, "www.naver.com");

		log.info("== set up finish ==");
	}
	
	@Test
	void 즐겨찾기_추가() {
		//given
		즐겨찾기(profileId, bookmarkId1);

		//when
		favoriteService.favorite(profileId, bookmarkId2);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId2);
		assertThat(result.isFavorite()).isEqualTo(true);
	}

	@Test
	void 즐겨찾기_해제() {
		//given
		즐겨찾기(profileId, bookmarkId1);

		//when
		favoriteService.unfavorite(profileId, bookmarkId1);

		//then
		final GetDetailedBookmarkResult result = 북마크_상세_조회(profileId, bookmarkId1);
		assertThat(result.isFavorite()).isEqualTo(false);
	}

	@Test
	void 즐겨찾기_추가_실패_이미_추가된_상태() {
		//given
		즐겨찾기(profileId, bookmarkId1);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> favoriteService.favorite(profileId, bookmarkId1));
	}
}
