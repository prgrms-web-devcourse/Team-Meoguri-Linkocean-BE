package com.meoguri.linkocean.domain.user.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

class UserRepositoryTest extends BasePersistenceTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void findByEmailAndOAuthType_성공() {
		//given
		사용자_저장("user@naver.com", NAVER);

		//when
		final Optional<User> oFoundUser = userRepository.findByEmailAndOAuthType(new Email("user@naver.com"), NAVER);

		//then
		assertThat(oFoundUser).isPresent();
		assertThat(oFoundUser.get().getEmail()).isEqualTo(new Email("user@naver.com"));
		assertThat(oFoundUser.get().getOauthType()).isEqualTo(NAVER);
	}
}
