package com.meoguri.linkocean.domain.profile.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Deprecated
@Query
@RequiredArgsConstructor
public class FindProfileByUserIdQuery {

	private final ProfileRepository profileRepository;

	public Profile findByUserId(long userId) {
		return profileRepository.findByUserId(userId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile with userId :%d", userId)));
	}
}
