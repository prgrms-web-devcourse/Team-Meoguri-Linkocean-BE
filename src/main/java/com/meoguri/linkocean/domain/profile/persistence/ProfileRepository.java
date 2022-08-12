package com.meoguri.linkocean.domain.profile.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	@Query("select p "
		+ "from Profile p "
		+ "where p.user.id = :userId")
	Optional<Profile> findByUserId(long userId);

	boolean existsByUsername(String username);
}

