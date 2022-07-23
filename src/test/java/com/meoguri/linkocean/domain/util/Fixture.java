package com.meoguri.linkocean.domain.util;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.OAuthType;
import com.meoguri.linkocean.domain.user.entity.User;

public final class Fixture {

	public static User createUser() {

		return new User(

			new Email("haha@papa.com"),
			OAuthType.GOOGLE
		);
	}

	public static Profile createProfile() {

		return new Profile(

			createUser(),
			"haha"
		);
	}
}
