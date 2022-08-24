package com.meoguri.linkocean.domain.profile.service.command;

import com.meoguri.linkocean.domain.profile.service.command.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.command.dto.UpdateProfileCommand;

public interface ProfileService {

	/* 프로필 등록 */
	long registerProfile(RegisterProfileCommand command);

	/* 프로필 업데이트 */
	void updateProfile(UpdateProfileCommand command);
}
