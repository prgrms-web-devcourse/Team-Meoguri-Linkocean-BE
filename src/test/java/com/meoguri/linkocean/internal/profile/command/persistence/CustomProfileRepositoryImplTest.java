package com.meoguri.linkocean.internal.profile.command.persistence;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.test.support.internal.persistence.BasePersistenceTest;

class CustomProfileRepositoryImplTest extends BasePersistenceTest {

	@Autowired
	private ProfileRepository profileRepository;

	private long profileId1;
	private long profileId2;

	@BeforeEach
	void setUp() {
		//set up 3 users
		Profile profile1 = 사용자_프로필_동시_저장("user1@gmail.com", GOOGLE, "user1", IT);
		Profile profile2 = 사용자_프로필_동시_저장("user2@naver.com", NAVER, "user2", IT);

		profileId1 = profile1.getId();
		profileId2 = profile2.getId();
	}

	@Test
	void 사용자_이름_중복_확인_성공() {
		final String savedUsername = "user1";
		final String unsavedUsername = "unsavedUsername";

		//when
		final boolean exists1 = profileRepository.existsByUsername(savedUsername);
		final boolean exists2 = profileRepository.existsByUsername(unsavedUsername);

		//then
		assertThat(exists1).isEqualTo(true);
		assertThat(exists2).isEqualTo(false);
	}

	@Test
	void existsByUsernameExceptMe_성공() {
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
}
