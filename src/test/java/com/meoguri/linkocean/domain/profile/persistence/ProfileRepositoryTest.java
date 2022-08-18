package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

class ProfileRepositoryTest extends BasePersistenceTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Test
	void 사용자_이름_중복_확인_성공() {
		//given
		final String savedUsername = "haha";
		사용자_프로필_저장_등록("haha@gmail.com", GOOGLE, savedUsername, IT, ART);

		//when
		final boolean exists1 = profileRepository.existsByUsername(savedUsername);
		final boolean exists2 = profileRepository.existsByUsername("unsavedUsername");

		//then
		assertThat(exists1).isTrue();
		assertThat(exists2).isFalse();
	}

	@Test
	void existsByUsernameExceptMe_성공() {
		//given
		long profileId1 = 사용자_프로필_저장_등록("user1@gmail.com", GOOGLE, "user1", IT).getId();
		long profileId2 = 사용자_프로필_저장_등록("user2@gmail.com", GOOGLE, "user2", IT).getId();

		//when
		final boolean exists1 = profileRepository.existsByUsernameExceptMe("user1", profileId1);
		final boolean exists2 = profileRepository.existsByUsernameExceptMe("user2", profileId1);
		final boolean exists3 = profileRepository.existsByUsernameExceptMe("user1", profileId2);
		final boolean exists4 = profileRepository.existsByUsernameExceptMe("user2", profileId2);

		//then
		assertThat(exists1).isFalse();
		assertThat(exists2).isTrue();
		assertThat(exists3).isTrue();
		assertThat(exists4).isFalse();
	}
}
