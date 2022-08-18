package com.meoguri.linkocean.domain.user.repository;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void 이메일과_벤더로_사용자_조회_성공() {
		//given
		final Email email = new Email("user@naver.com");
		final OAuthType oAuthType = NAVER;
		userRepository.save(new User(email, oAuthType));

		//when
		final Optional<User> foundUser = userRepository.findByEmailAndOAuthType(email, oAuthType);

		//then
		assertThat(foundUser).isPresent().get()
			.extracting(User::getEmail, User::getOauthType)
			.containsExactly(email, oAuthType);
	}

}
