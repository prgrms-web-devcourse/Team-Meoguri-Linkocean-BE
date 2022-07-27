package com.meoguri.linkocean.domain.user.repository;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindUserByIdQuery {

	private final UserRepository userRepository;

	public User findById(long id) {
		return userRepository.findById(id).orElseThrow(LinkoceanRuntimeException::new);
	}
}
