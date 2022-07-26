package com.meoguri.linkocean.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(Email email);
}
