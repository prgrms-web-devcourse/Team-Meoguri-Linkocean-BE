package com.meoguri.linkocean.domain.user.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class FindUserByIdQuery {

	private final UserRepository userRepository;

	public User findById(long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such user id :%d", id)));
	}
}
