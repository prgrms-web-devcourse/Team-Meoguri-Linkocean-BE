package com.meoguri.linkocean.domain.profile.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	/* 사용자 아이디로 프로필 조회 */
	// TODO - Note - implicit cross join occurs in this query
	@Query("select p "
		+ "from Profile p "
		+ "where p.user.id = :userId")
	Optional<Profile> findByUserId(long userId);

	/* 사용자 이름 중복 확인 */
	boolean existsByUsername(String username);

	@Query("select count(p) > 0 "
		+ "from Profile p "
		+ "where p.username = :updateUsername "
		+ "and not p.id = :profileId")
	boolean existsByUsernameExceptMe(String updateUsername, long profileId);
}

