package com.meoguri.linkocean.domain.profile.query.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FindProfileByIdRepository extends Repository<Profile, Long> {

	@NonNull
	Profile getById(long id);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds.favoriteBookmarkIds "
		+ "where p.id = :profileId")
	Profile getProfileFetchFavoriteIdsById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.follows "
		+ "where p.id = :profileId")
	Profile getProfileFetchFollows(long profileId);

}
