package com.meoguri.linkocean.domain.user.repository;

import static com.meoguri.linkocean.domain.user.entity.User.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindUserByEmailAndTypeQuery {

	private final UserRepository userRepository;

	public User findUserByUserAndType(Email email, OAuthType oAuthType) {
		return userRepository.findByEmailAndOAuthType(email, oAuthType)
			.orElseThrow(LinkoceanRuntimeException::new);
	}
}
