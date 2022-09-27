package com.meoguri.linkocean.internal.profile.query.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.internal.profile.entity.Profile;

public interface ProfileQueryRepository extends JpaRepository<Profile, Long>, CustomProfileQueryRepository {

	/* user 를 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.id.followerId = :profileId")
	int getFolloweeCount(long profileId);

	/* user 가 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) "
		+ "from Follow f "
		+ "where f.id.followeeId = :profileId")
	int getFollowerCount(long profileId);
}
