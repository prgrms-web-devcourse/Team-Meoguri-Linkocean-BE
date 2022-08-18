package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

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
		user.registerProfile(profile);

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

	@Test
	void 사용자_이름_변경_남이름을_먹으려_하면_실패() {
		//given
		User user1 = createUser("user1@gmail.com", GOOGLE);
		User user2 = createUser("user2@gmail.com", GOOGLE);

		user1 = userRepository.save(user1);
		user2 = userRepository.save(user2);

		Profile profile1 = new Profile("user1", List.of(Category.IT));
		Profile profile2 = new Profile("user2", List.of(Category.IT));

		profile1 = profileRepository.save(profile1);
		profile2 = profileRepository.save(profile2);

		user1.registerProfile(profile1);
		user2.registerProfile(profile2);

		//when
		final boolean exists1 = profileRepository.existsByUsernameExceptMe("user1", profile1.getId());
		final boolean exists2 = profileRepository.existsByUsernameExceptMe("user2", profile1.getId());
		final boolean exists3 = profileRepository.existsByUsernameExceptMe("user1", profile2.getId());
		final boolean exists4 = profileRepository.existsByUsernameExceptMe("user2", profile2.getId());

		//then
		assertThat(exists1).isFalse();
		assertThat(exists2).isTrue();
		assertThat(exists3).isTrue();
		assertThat(exists4).isFalse();
	}
}
