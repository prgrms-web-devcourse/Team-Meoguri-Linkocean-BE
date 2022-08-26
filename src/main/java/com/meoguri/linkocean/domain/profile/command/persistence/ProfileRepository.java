package com.meoguri.linkocean.domain.profile.command.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.command.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds.favoriteBookmarkIds "
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

	/* Note - favorite 과 reaction 모두 set 으로 관리되기 때문에
	MultipleBagFetchException 발생하지 않음 but Query 는 세번 발생
	1. 프로필 조회, 2. 즐겨찾기 조회, 3. 리액션 조회*/
	@Query("select p "
		+ "from Profile p "
		+ "left join fetch p.favoriteBookmarkIds "
		+ "left join fetch p.reactions "
		+ "where p.id = :profileId")
	Optional<Profile> findProfileFetchFavoriteAndReactionById(long profileId);

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

