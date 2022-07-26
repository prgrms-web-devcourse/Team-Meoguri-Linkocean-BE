package com.meoguri.linkocean.domain.profile.persistence;

import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProfileByUserIdQuery {

	private final ProfileRepository profileRepository;

	public Profile getByUserId(long userId) {
		return profileRepository.findByUserId(userId).orElseThrow(LinkoceanRuntimeException::new);
	}
}
