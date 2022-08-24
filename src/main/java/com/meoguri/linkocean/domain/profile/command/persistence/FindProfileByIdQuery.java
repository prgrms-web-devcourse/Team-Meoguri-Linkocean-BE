package com.meoguri.linkocean.domain.profile.command.persistence;

import static java.lang.String.*;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
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

	public Profile findProfileFetchReactionById(long profileId) {
		return profileRepository.findProfileFetchReactionById(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}

	public Profile findProfileFetchFollows(long profileId) {
		return profileRepository.findProfileFetchFollows(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}

	public Profile findProfileFetchFavoriteAndReactionById(long profileId) {
		return profileRepository.findProfileFetchFavoriteAndReactionById(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}
}
