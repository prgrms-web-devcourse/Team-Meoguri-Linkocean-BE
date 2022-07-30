package com.meoguri.linkocean.domain.util;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;

public final class Fixture {

	public static User createUser() {

		return new User(

			"haha@papa.com", "GOOGLE");
	}

	public static Profile createProfile() {
		return createProfile(createUser());
	}

	public static Profile createProfile(User user) {

		return new Profile(

			user, "haha");
	}

	public static Bookmark createBookmark() {

		return Bookmark.builder()
			.profile(createProfile())
			.title("title")
			.linkMetadata(createLinkMetadata())
			.memo("dream company")
			.category("it")
			.openType("all")
			.build();
	}

	public static LinkMetadata createLinkMetadata() {

		return new LinkMetadata(

			"google.com", "구글", "google.png");
	}

	public static Tag createTag() {

		return new Tag("tag");
	}
}
