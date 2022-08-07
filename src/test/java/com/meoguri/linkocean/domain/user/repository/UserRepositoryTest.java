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
		final String email = "user@naver.com";
		final String oAuthType = "NAVER";

		final User user = new User(email, oAuthType);
		userRepository.save(user);

		//when
		final Optional<User> foundUser = userRepository.findByEmailAndOAuthType(new Email(email),
			OAuthType.of(oAuthType));

		//then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get()).extracting(User::getEmail, User::getOauthType)
			.isEqualTo(List.of(new Email(email), OAuthType.of(oAuthType)));
	}

}
