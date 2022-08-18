package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public interface UserService {

	void saveIfNotExists(Email email, OAuthType oAuthType);

	void registerProfile(long userId, Profile profile);
}
