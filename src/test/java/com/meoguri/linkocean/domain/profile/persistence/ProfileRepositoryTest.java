package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@DataJpaTest
class ProfileRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	private User user;

	@BeforeEach
	void setUp() {
		user = userRepository.save(createUser());
	}

	@Test
	void 사용자_아이디로_프로필_조회_성공() {
		//given
		final Profile profile = new Profile(user, "haha");
		profileRepository.save(profile);

		//when
		final Optional<Profile> foundProfile = profileRepository.findByUserId(user.getId());

		//then
		assertThat(foundProfile).isPresent();
		assertThat(foundProfile.get()).isEqualTo(profile);
	}

	@Test
	void 사용자_이름_중복_확인() {
		//given
		final Profile profile = new Profile(user, "haha");
		profileRepository.save(profile);

		//when
		final boolean exists1 = profileRepository.existsByUsername("haha");
		final boolean exists2 = profileRepository.existsByUsername("papa");

		//then
		assertThat(exists1).isTrue();
		assertThat(exists2).isFalse();
	}
}
