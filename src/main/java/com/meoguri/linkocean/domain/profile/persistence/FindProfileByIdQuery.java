package com.meoguri.linkocean.domain.profile.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindProfileByIdQuery {

	private final ProfileRepository profileRepository;

	public Profile findById(long profileId) {
		return profileRepository.findById(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}

	public Profile findProfileFetchFavoriteById(long profileId) {
		return profileRepository.findProfileFetchFavoriteIdsById(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}
}
