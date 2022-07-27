package com.meoguri.linkocean.domain.profile.persistence;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindProfileByIdQuery {

	private final ProfileRepository profileRepository;

	public Profile findById(long id) {
		return profileRepository.findById(id).orElseThrow(LinkoceanRuntimeException::new);
	}
}
