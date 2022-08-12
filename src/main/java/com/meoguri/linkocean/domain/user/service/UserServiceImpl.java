package com.meoguri.linkocean.domain.user.service;

import static java.lang.String.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.configuration.security.jwt.JwtProvider;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.repository.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	@Transactional
	@Override
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

	@Transactional
	@Override
	public void registerProfile(final long userId, final Profile profile) {

		final User user = userRepository.findById(userId)
			.orElseThrow((() -> new LinkoceanRuntimeException(format("no such user id :%d", userId))));

		user.registerProfile(profile);
	}
}
