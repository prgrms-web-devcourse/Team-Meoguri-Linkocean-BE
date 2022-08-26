package com.meoguri.linkocean.domain.profile.command.persistence;

import static java.lang.String.*;

import java.util.Optional;
import java.util.function.Function;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class FindProfileByIdQuery {

	private final ProfileRepository profileRepository;

	public Profile findById(long profileId) {
		return findProfileById(profileId, profileRepository::findById);
	}

	public Profile findProfileFetchFavoriteById(long profileId) {
		return findProfileById(profileId, profileRepository::findProfileFetchFavoriteIdsById);
	}

	public Profile findProfileFetchReactionById(long profileId) {
		return findProfileById(profileId, profileRepository::findProfileFetchReactionById);
	}

	public Profile findProfileFetchFollows(long profileId) {
		return findProfileById(profileId, profileRepository::findProfileFetchFollows);
	}

	public Profile findProfileFetchFavoriteAndReactionById(long profileId) {
		return findProfileById(profileId, profileRepository::findProfileFetchFavoriteAndReactionById);
	}

	private Profile findProfileById(long profileId, Function<Long, Optional<Profile>> findById) {
		return findById.apply(profileId)
			.orElseThrow(() -> new LinkoceanRuntimeException(format("no such profile id :%d", profileId)));
	}
}
