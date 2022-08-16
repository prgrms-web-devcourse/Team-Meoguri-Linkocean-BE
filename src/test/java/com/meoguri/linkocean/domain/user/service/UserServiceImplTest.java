package com.meoguri.linkocean.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;

@Transactional
@SpringBootTest
class UserServiceImplTest {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 새로운_사용자_저장_성공() {
		//given
		final String email = "crush@github.com";
		final String oAuthType = "GITHUB";

		//when
		final String token = userService.saveOrUpdate(email, oAuthType);

		//then
		final Optional<User> retrievedUser = userRepository.findByEmailAndOAuthType(new Email(email),
			OAuthType.of(oAuthType));

		assertAll(
			() -> assertThat(retrievedUser).isPresent(),
			() -> assertThat(retrievedUser.get())
				.extracting(User::getEmail, User::getOauthType)
				.containsExactly(new Email(email), OAuthType.of(oAuthType)),
			() -> assertThat(jwtProvider.getClaims(token, Claims::getAudience)).isEqualTo(oAuthType)
		);
	}
}