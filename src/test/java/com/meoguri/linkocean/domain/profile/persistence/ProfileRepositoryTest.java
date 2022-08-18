package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

class ProfileRepositoryTest extends BasePersistenceTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Test
	void 사용자_아이디로_프로필_조회_성공() {
		//given
		User user = 사용자_저장("haha@gmail.com", GOOGLE);
		Profile profile = 프로필_저장_등록(user, "haha", IT, ART);

		//when
		final Optional<Profile> oFoundProfile = profileRepository.findByUserId(user.getId());

		//then
		assertThat(oFoundProfile).isPresent().get().isEqualTo(profile);
	}

	@Test
	void 사용자_이름_중복_확인_성공() {
		//given
		프로필_저장("haha", IT, ART);

		//when
		final boolean exists1 = profileRepository.existsByUsername("haha");
		final boolean exists2 = profileRepository.existsByUsername("papa");

		//then
		assertThat(exists1).isTrue();
		assertThat(exists2).isFalse();
	}

	@Test
	void existsByUsernameExceptMe_성공() {

		//given
		User user1 = 사용자_저장("user1@gmail.com", GOOGLE);
		User user2 = 사용자_저장("user2@gmail.com", GOOGLE);

		long profileId1 = 프로필_저장_등록(user1, "user1", IT).getId();
		long profileId2 = 프로필_저장_등록(user2, "user2", IT).getId();

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
