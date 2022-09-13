package com.meoguri.linkocean.domain.profile.query.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.support.domain.persistence.aop.RequireSingleResult;

@RequireSingleResult
public interface FindProfileByIdRepository extends Repository<Profile, Long> {

	Profile findById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds.favoriteBookmarkIds "
		+ "where p.id = :profileId")
	Profile findProfileFetchFavoriteIdsById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.follows "
		+ "where p.id = :profileId")
	Profile findProfileFetchFollows(long profileId);

}
