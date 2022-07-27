package com.meoguri.linkocean.domain.profile.persistence;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindProfileByUserIdQuery {

	private final ProfileRepository profileRepository;

	public Profile findByUserId(long userId) {
		return profileRepository.findByUserId(userId).orElseThrow(LinkoceanRuntimeException::new);
	}
}
