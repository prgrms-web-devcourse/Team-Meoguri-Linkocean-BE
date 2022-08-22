package com.meoguri.linkocean.domain.profile.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	/* 사용자 이름 중복 확인 */
	boolean existsByUsername(String username);

	@Query("select count(p) > 0 "
		+ "from Profile p "
		+ "where p.username = :updateUsername "
		+ "and not p.id = :profileId")
	boolean existsByUsernameExceptMe(String updateUsername, long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFavoriteIdsById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.reactions "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchReactionById(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.follows "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFollows(long profileId);

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds "
		+ "left join fetch p.follows  "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFavoriteAndFollowsById(long profileId);

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

