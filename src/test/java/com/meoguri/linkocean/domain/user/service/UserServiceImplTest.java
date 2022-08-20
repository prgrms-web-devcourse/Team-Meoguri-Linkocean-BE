package com.meoguri.linkocean.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Transactional
@SpringBootTest
class UserServiceImplTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 새로운_사용자_저장_성공() {
		//given
		final Email email = new Email("crush@github.com");
		final OAuthType oAuthType = OAuthType.GITHUB;

		//when
		userService.registerIfNotExists(email, oAuthType);

		//then
		final Optional<User> oFindUser = userRepository.findByEmailAndOAuthType(email, oAuthType);
		assertThat(oFindUser).isPresent().get()
			.extracting(User::getEmail, User::getOauthType)
			.containsExactly(email, oAuthType);
	}
}
