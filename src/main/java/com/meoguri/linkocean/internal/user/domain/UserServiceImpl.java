package com.meoguri.linkocean.internal.user.domain;

import static java.lang.String.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;
import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.internal.user.domain.dto.GetUserResult;
import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;
import com.meoguri.linkocean.internal.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public GetUserResult getUser(final Email email, final OAuthType oAuthType) {
		final User user = userRepository.findByEmailAndOAuthType(email, oAuthType)
			.orElseThrow(() -> new IllegalArgumentException(format("no such user email : %s", Email.toString(email))));

		return new GetUserResult(
			user.getId(),
			user.getProfileId(),
			user.getEmail(),
			user.getOauthType()
		);
	}

	@Override
	public User getUser(final long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new LinkoceanRuntimeException("존재하지 않는 사용자 아이디입니다."));
	}

	@Transactional
	@Override
	public long registerIfNotExists(final Email email, final OAuthType oAuthType) {
		return userRepository.findByEmailAndOAuthType(email, oAuthType)
			.orElseGet(() -> {
				log.info("new user save email : {}, oauth type : {}", Email.toString(email), oAuthType);
				return userRepository.save(new User(email, oAuthType));
			}).getId();
	}

	@Transactional
	@Override
	public void registerProfile(final long userId, final Profile profile) {
		final User user = userRepository.findById(userId)
			.orElseThrow((() -> new LinkoceanRuntimeException(format("no such user id :%d", userId))));

		user.registerProfile(profile);
	}
}
