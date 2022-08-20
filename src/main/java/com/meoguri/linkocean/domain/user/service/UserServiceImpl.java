package com.meoguri.linkocean.domain.user.service;

import static java.lang.String.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Transactional
	@Override
	public void registerIfNotExists(final Email email, final OAuthType oAuthType) {
		userRepository.findByEmailAndOAuthType(email, oAuthType)
			.orElseGet(() -> {
				log.info("new user save email : {}, oauth type : {}", Email.toString(email), oAuthType);
				return userRepository.save(new User(email, oAuthType));
			});
	}

	@Transactional
	@Override
	public void registerProfile(final long userId, final Profile profile) {
		final User user = userRepository.findById(userId)
			.orElseThrow((() -> new LinkoceanRuntimeException(format("no such user id :%d", userId))));

		user.registerProfile(profile);
	}
}
