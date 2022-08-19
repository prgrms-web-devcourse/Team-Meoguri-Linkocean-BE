package com.meoguri.linkocean.domain.profile.persistence;

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
}

