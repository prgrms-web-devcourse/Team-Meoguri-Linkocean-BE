package com.meoguri.linkocean.domain.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.user.domain.model.Email;
import com.meoguri.linkocean.domain.user.domain.model.OAuthType;
import com.meoguri.linkocean.domain.user.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.email = :email and u.oauthType = :oauthType")
	Optional<User> findByEmailAndOAuthType(Email email, OAuthType oauthType);
}
