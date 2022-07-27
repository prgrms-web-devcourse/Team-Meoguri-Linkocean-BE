package com.meoguri.linkocean.domain.profile.service;

import com.meoguri.linkocean.domain.profile.service.dto.ProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;

public interface ProfileService {

	long registerProfile(RegisterProfileCommand command);

	ProfileResult getProfileByUserId(long userId);

	void updateProfile(UpdateProfileCommand command);
}
