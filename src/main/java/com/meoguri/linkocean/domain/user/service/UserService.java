package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public interface UserService {

	/* 없으면 등록 */
	void registerIfNotExists(Email email, OAuthType oAuthType);

	/* 프로필 등록 - user 에게 profileId 를 등록한다 */
	void registerProfile(long userId, Profile profile);
}
