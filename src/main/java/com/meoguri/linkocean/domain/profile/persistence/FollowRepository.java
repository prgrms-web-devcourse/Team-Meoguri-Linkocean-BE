package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerAndFollowee(Profile follower, Profile followee);

	@Query("select f from Follow f where f.follower = :follower and f. followee = :followee")
	Optional<Follow> findByProfiles(Profile follower, Profile followee);

	/* user 를 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) from Follow f where f.followee =:profile")
	int countFollowerByProfile(Profile profile);

	/* user 가 팔로우 하는 사용자의 카운트 */
	@Query("select count(f) from Follow f where f.follower =:profile")
	int countFolloweeByProfile(Profile profile);

	/* 팔로우중인 대상의 프로필 아이디 목록 조회 */
	@Query("select f.followee.id from Follow f where f.follower.id = :followerId")
	List<Long> findAllFolloweeIdByFollowerId(long followerId);

}
