package com.meoguri.linkocean.domain.profile.query.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.domain.persistence.BasePersistenceTest;

class FindProfileByIdRepositoryTest extends BasePersistenceTest {

	@Autowired
	private FindProfileByIdRepository findProfileByIdRepository;

	@Test
	void getById_성공() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);

		//when
		final Profile findProfile = findProfileByIdRepository.getById(profile.getId());

		//then
		assertThat(findProfile).isEqualTo(profile);
	}

	@Test
	void getById_실패_유효하지_않은_id() {
		final Profile profile = findProfileByIdRepository.getById(-1L);
		assertThat(isLoaded(profile)).isFalse();
	}

	@Test
	void getProfileFetchFavoriteIdsById_성공() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
		final Bookmark bookmark1 = 북마크_링크_메타데이터_동시_저장(profile, "title1", ALL, IT, "www.naver.com");
		final Bookmark bookmark2 = 북마크_링크_메타데이터_동시_저장(profile, "title2", ALL, HOME, "www.google.com");
		final Bookmark bookmark3 = 북마크_링크_메타데이터_동시_저장(profile, "title3", PRIVATE, IT, "www.github.com");

		즐겨찾기_저장(profile, bookmark1);
		즐겨찾기_저장(profile, bookmark3);

		//when
		final Profile findProfile = findProfileByIdRepository.getProfileFetchFavoriteIdsById(profile.getId());

		//then
		assertThat(findProfile.isFavoriteBookmarks(of(bookmark1, bookmark2, bookmark3)))
			.containsExactly(true, false, true);
	}

	@Test
	void getProfileFetchFavoriteIdsById_성공_즐겨찾기가_없어도() {
		//given
		final Profile savedProfile = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);

		//when
		final Profile findProfile = findProfileByIdRepository.getProfileFetchFavoriteIdsById(savedProfile.getId());

		//then
		assertThat(findProfile).isEqualTo(savedProfile);
	}

	@Test
	void getProfileFetchFollows_성공() {
		//given
		final Profile follower = 사용자_프로필_동시_저장("follower@gmail.com", GOOGLE, "follower", IT);
		final Profile profile1 = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
		final Profile profile2 = 사용자_프로필_동시_저장("user2@gmail.com", GOOGLE, "user2", IT);
		final Profile profile3 = 사용자_프로필_동시_저장("user3@gmail.com", GOOGLE, "user3", IT);
		final Profile profile4 = 사용자_프로필_동시_저장("user4@gmail.com", GOOGLE, "user4", IT);

		팔로우_저장(follower, profile1);
		팔로우_저장(follower, profile2);
		팔로우_저장(follower, profile3);

		//when
		final Profile profile = findProfileByIdRepository.getProfileFetchFollows(follower.getId());

		//then
		assertThat(profile.isFollows(List.of(profile1, profile2, profile3, profile4)))
			.containsExactly(true, true, true, false);
	}
}
