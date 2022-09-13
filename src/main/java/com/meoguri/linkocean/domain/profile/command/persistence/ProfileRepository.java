package com.meoguri.linkocean.domain.profile.command.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	Profile findById(long profileId);
}

