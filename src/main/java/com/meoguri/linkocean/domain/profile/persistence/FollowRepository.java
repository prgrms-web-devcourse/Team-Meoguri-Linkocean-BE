package com.meoguri.linkocean.domain.profile.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Query("select f from Follow f where f.follower = :follower and f. followee = :followee")
	Optional<Follow> findByProfiles(Profile follower, Profile followee);
}
