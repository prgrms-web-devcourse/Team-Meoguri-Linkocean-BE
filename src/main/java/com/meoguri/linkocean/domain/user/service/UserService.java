package com.meoguri.linkocean.domain.user.service;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.User.OAuthType;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public String saveOrUpdate(final String email, final String oAuthType) {
		log.info("user save start email : {} ", email);
		final Email emailField = new Email(email);
		final OAuthType oAuthTypeField = OAuthType.of(oAuthType.toUpperCase());

		userRepository.findByEmailAndOAuthType(emailField, oAuthTypeField)
			.orElseGet(() -> {
				final User savedUser = userRepository.save(new User(email, oAuthType.toUpperCase()));
				log.info("새로운 사용자 저장 email : {}, oauth type : {}",
					Email.toString(savedUser.getEmail()), savedUser.getOauthType());
				return savedUser;
			});
		return jwtProvider.generate(email, oAuthType);
	}

}
