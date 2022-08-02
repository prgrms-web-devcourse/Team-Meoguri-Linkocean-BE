package com.meoguri.linkocean.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.User.OAuthType;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void 이메일과_벤터로_사용자_조회_성공() {
		//given
		final Email email = new Email("user@naver.com");
		final OAuthType oAuthType = OAuthType.NAVER;

		final User user = new User(email, oAuthType);
		userRepository.save(user);

		//when
		final Optional<User> foundUser = userRepository.findByEmailAndOAuthType(email, oAuthType);

		//then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get()).extracting(User::getEmail, User::getOAuthType)
			.isEqualTo(List.of(email, oAuthType));
	}

}
