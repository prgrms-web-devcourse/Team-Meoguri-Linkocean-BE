package com.meoguri.linkocean.domain.util;

import static com.meoguri.linkocean.domain.bookmark.entity.OpenType.*;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.OAuthType;
import com.meoguri.linkocean.domain.user.entity.User;

public final class Fixture {

	public static User createUser() {

		return new User(

			"haha@papa.com",
			OAuthType.GOOGLE
		);
	}

	public static Profile createProfile() {

		return new Profile(

			createUser(),
			"haha"
		);
	}

	public static Bookmark createBookmark() {

		return Bookmark.builder()
			.profile(createProfile())
			.title("title")
			.url("www.google.com")
			.memo("dream company")
			.openType(ALL)
			.build();
	}

	public static Tag createTag() {

		return new Tag("tag");
	}
}
