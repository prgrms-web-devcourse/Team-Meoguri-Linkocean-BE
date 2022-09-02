package com.meoguri.linkocean.domain.profile.query.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileQueryRepository extends JpaRepository<Profile, Long>, CustomProfileQueryRepository {

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds.favoriteBookmarkIds "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFavoriteIdsById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.follows "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFollows(long profileId);

	/* user 를 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.id.follower = :profile")
	int getFolloweeCount(Profile profile);

	/* user 가 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.id.followee = :profile")
	int getFollowerCount(Profile profile);
}