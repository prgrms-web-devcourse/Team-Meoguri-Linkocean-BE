package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class FavoriteRepositoryTest extends BasePersistenceTest {

	@Autowired
	private FavoriteRepository favoriteRepository;

	private Profile profile;
	private long profileId;

	@BeforeEach
	void setUp() {
		profile = 사용자_프로필_저장_등록("haha@gmail.com", GOOGLE, "haha", IT, ART);
		profileId = profile.getId();
	}

	@Test
	void 페이보릿_저장_삭제_성공() {
		//given
		final Bookmark naver = 북마크_링크_메타데이터_저장(profile, "www.naver.com");
		즐겨찾기_저장(profile, naver);

		//when
		final int deletedCount = favoriteRepository.deleteByProfile_idAndBookmark_id(profileId, naver.getId());

		//then
		assertThat(deletedCount).isEqualTo(1);
		assertThat(favoriteRepository.findAll()).isEmpty();
	}

	@Test
	void existsByProfile_idAndBookmark_성공() {
		//given
		final Bookmark naver = 북마크_링크_메타데이터_저장(profile, "www.naver.com");
		final Bookmark github = 북마크_링크_메타데이터_저장(profile, "www.github.com");
		즐겨찾기_저장(profile, naver);

		//when
		final boolean isFavorite1 = favoriteRepository.existsByProfile_idAndBookmark(profileId, naver);
		final boolean isFavorite2 = favoriteRepository.existsByProfile_idAndBookmark(profileId, github);

		//then
		assertThat(isFavorite1).isTrue();
		assertThat(isFavorite2).isFalse();
	}

	@Test
	void 즐겨찾기_중인_북마크의_id_집합_조회_성공() {
		//given
		final Bookmark naver = 북마크_링크_메타데이터_저장(profile, "www.naver.com");
		final Bookmark github = 북마크_링크_메타데이터_저장(profile, "www.github.com");
		즐겨찾기_저장(profile, naver);

		//when
		final Set<Long> favoriteBookmarkIds =
			favoriteRepository.findBookmarkIdByProfileIdAndInBookmarks(profileId, List.of(naver, github));

		//then
		assertThat(favoriteBookmarkIds).containsExactly(naver.getId());
	}
}
