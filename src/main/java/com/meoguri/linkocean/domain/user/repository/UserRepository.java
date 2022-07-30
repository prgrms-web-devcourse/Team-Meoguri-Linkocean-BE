package com.meoguri.linkocean.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.email = :email and u.oAuthType = :oAuthType")
	Optional<User> findByEmailAndOAuthType(Email email, OAuthType oAuthType);
}
