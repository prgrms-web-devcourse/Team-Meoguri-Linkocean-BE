package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface UserService {

	String saveOrUpdate(String email, String oAuthType);

	void registerProfile(long userId, Profile profile);
}
