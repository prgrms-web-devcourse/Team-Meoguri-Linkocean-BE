package com.meoguri.linkocean.domain.user.service;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.model.Email;
import com.meoguri.linkocean.domain.user.model.OAuthType;
import com.meoguri.linkocean.domain.user.service.dto.GetUserResult;

public interface UserService {

	/* 사용자 조회 */
	GetUserResult getUser(Email email, OAuthType oAuthType);

	/* 사용자 없으면 등록 */
	long registerIfNotExists(Email email, OAuthType oAuthType);

	/* 프로필 등록 - user 에게 profileId 를 등록한다 */
	void registerProfile(long userId, Profile profile);
}
