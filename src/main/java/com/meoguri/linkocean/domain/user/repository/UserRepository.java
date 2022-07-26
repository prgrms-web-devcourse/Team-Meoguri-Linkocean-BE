package com.meoguri.linkocean.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
