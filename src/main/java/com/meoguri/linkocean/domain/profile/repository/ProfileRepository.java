package com.meoguri.linkocean.domain.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	@Query("select p from Profile p where p.user.id = :userId")
	Optional<Profile> findByUserId(long userId);
}

