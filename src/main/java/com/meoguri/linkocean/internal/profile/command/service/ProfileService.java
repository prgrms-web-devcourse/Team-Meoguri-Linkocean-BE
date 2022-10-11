package com.meoguri.linkocean.internal.profile.command.service;

import com.meoguri.linkocean.internal.profile.command.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.internal.profile.command.service.dto.UpdateProfileCommand;

public interface ProfileService {

	/* 프로필 등록 */
	long registerProfile(RegisterProfileCommand command);

	/* 프로필 업데이트 */
	void updateProfile(UpdateProfileCommand command);
}
