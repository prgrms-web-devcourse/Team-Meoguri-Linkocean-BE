package com.meoguri.linkocean.domain.profile;

import java.util.List;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;

public final class ProfileCommand {

	public static RegisterProfileCommand ofRegister(Profile profile, List<String> categories) {

		return new RegisterProfileCommand(
			profile.getUser().getId(),
			profile.getUsername(),
			categories
		);
	}
}
