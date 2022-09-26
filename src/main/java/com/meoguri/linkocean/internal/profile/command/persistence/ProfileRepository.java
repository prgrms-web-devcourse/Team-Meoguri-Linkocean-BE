package com.meoguri.linkocean.internal.profile.command.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.internal.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

	Profile findById(long profileId);
}

