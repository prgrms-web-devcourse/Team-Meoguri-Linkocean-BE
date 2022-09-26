package com.meoguri.linkocean.internal.user.domain;

import static com.meoguri.linkocean.internal.user.domain.model.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.User;
import com.meoguri.linkocean.test.support.internal.persistence.BasePersistenceTest;

class UserRepositoryTest extends BasePersistenceTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void findByEmailAndOAuthType_성공() {
		//given
		final Email email = new Email("user@naver.com");
		사용자_저장(Email.toString(email), NAVER);

		//when
		final Optional<User> oFoundUser = pretty(() -> userRepository.findByEmailAndOAuthType(email, NAVER));

		//then
		assertThat(oFoundUser).isPresent();
		assertThat(oFoundUser.get().getEmail()).isEqualTo(email);
		assertThat(oFoundUser.get().getOauthType()).isEqualTo(NAVER);
	}
}
