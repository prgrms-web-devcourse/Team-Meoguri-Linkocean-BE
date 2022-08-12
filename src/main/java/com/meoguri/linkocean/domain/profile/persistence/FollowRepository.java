package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollower_idAndFollowee(long followerId, Profile followee);

	@Query("select f "
		+ "from Follow f "
		+ "where f.follower = :follower and f. followee = :followee")
	Optional<Follow> findByProfiles(Profile follower, Profile followee);

	/* user 를 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.followee = :profile")
	int countFollowerByProfile(Profile profile);

	/* user 가 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.follower = :profile")
	int countFolloweeByProfile(Profile profile);

	/* profileId 가 팔로우 중인 사용자의 id 집합 조회 */
	@Query("select f.followee.id "
		+ "from Follow f "
		+ "where f.follower.id = :profileId and f.followee in :targets")
	Set<Long> findFolloweeIdsFollowedBy(long profileId, List<Profile> targets);

	long deleteByFollower_idAndFollowee_id(long followerId, long followeeId);
}
