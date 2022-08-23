package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class ProfileRepositoryTest extends BasePersistenceTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Test
	void 사용자_이름_중복_확인_성공() {
		//given
		final String savedUsername = "haha";
		사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, savedUsername, IT, ART);

		//when
		final boolean exists1 = profileRepository.existsByUsername(savedUsername);
		final boolean exists2 = profileRepository.existsByUsername("unsavedUsername");

		//then
		assertThat(exists1).isEqualTo(true);
		assertThat(exists2).isEqualTo(false);
	}

	@Test
	void existsByUsernameExceptMe_성공() {
		//given
		long profileId1 = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT).getId();
		long profileId2 = 사용자_프로필_동시_저장("user2@gmail.com", GOOGLE, "user2", IT).getId();

		//when
		final boolean exists1 = profileRepository.existsByUsernameExceptMe("user1", profileId1);
		final boolean exists2 = profileRepository.existsByUsernameExceptMe("user2", profileId1);
		final boolean exists3 = profileRepository.existsByUsernameExceptMe("user1", profileId2);
		final boolean exists4 = profileRepository.existsByUsernameExceptMe("user2", profileId2);

		//then
		assertThat(exists1).isEqualTo(false);
		assertThat(exists2).isEqualTo(true);
		assertThat(exists3).isEqualTo(true);
		assertThat(exists4).isEqualTo(false);
	}

	@Test
	void findProfileFetchFavoriteIdsById_성공() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
		final Bookmark bookmark1 = 북마크_링크_메타데이터_동시_저장(profile, "title1", ALL, IT, "www.naver.com");
		final Bookmark bookmark2 = 북마크_링크_메타데이터_동시_저장(profile, "title2", PARTIAL, HOME, "www.google.com");
		final Bookmark bookmark3 = 북마크_링크_메타데이터_동시_저장(profile, "title3", PRIVATE, IT, "www.github.com");

		즐겨찾기_저장(profile, bookmark1);
		즐겨찾기_저장(profile, bookmark3);

		//when
		final Optional<Profile> oProfile = profileRepository.findProfileFetchFavoriteIdsById(profile.getId());

		//then
		assertThat(oProfile).isPresent();
		assertThat(oProfile.get().isFavoriteBookmarks(of(bookmark1, bookmark2, bookmark3)))
			.containsExactly(true, false, true);
	}

	@Test
	void findProfileFetchFavoriteIdsById_성공_즐겨찾기가_없어도() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);

		//when
		final Optional<Profile> oProfile = profileRepository.findProfileFetchFavoriteIdsById(profile.getId());

		//then
		assertThat(oProfile).isPresent();
	}

	@Test
	void findProfileFetchFavoriteAndReactionById_성공() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
		final Bookmark bookmark1 = 북마크_링크_메타데이터_동시_저장(profile, "title1", ALL, IT, "www.naver.com");
		final Bookmark bookmark2 = 북마크_링크_메타데이터_동시_저장(profile, "title2", PARTIAL, HOME, "www.google.com");
		final Bookmark bookmark3 = 북마크_링크_메타데이터_동시_저장(profile, "title3", PRIVATE, IT, "www.github.com");

		즐겨찾기_저장(profile, bookmark1);
		즐겨찾기_저장(profile, bookmark3);

		싫어요_저장(profile, bookmark1);
		좋아요_저장(profile, bookmark2);

		//when
		final Optional<Profile> oProfile = profileRepository.findProfileFetchFavoriteAndReactionById(profile.getId());

		//then
		final Profile findProfile = oProfile.get();
		assertThat(findProfile.isFavoriteBookmarks(of(bookmark1, bookmark2, bookmark3)))
			.containsExactly(true, false, true);

		assertThat(findProfile.checkReaction(bookmark1)).containsAllEntriesOf(Map.of(LIKE, false, HATE, true));
		assertThat(findProfile.checkReaction(bookmark2)).containsAllEntriesOf(Map.of(LIKE, true, HATE, false));
		assertThat(findProfile.checkReaction(bookmark3)).containsAllEntriesOf(Map.of(LIKE, false, HATE, false));
	}

}
