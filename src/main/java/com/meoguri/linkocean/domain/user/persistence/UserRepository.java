package com.meoguri.linkocean.domain.user.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.user.model.Email;
import com.meoguri.linkocean.domain.user.model.OAuthType;
import com.meoguri.linkocean.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.email = :email and u.oauthType = :oauthType")
	Optional<User> findByEmailAndOAuthType(Email email, OAuthType oauthType);
}
