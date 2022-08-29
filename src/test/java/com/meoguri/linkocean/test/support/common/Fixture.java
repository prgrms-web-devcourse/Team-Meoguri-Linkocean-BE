package com.meoguri.linkocean.test.support.common;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.tag.entity.Tag;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public final class Fixture {

	public static User createUser(String email, OAuthType oauthType) {
		return new User(new Email(email), oauthType);
	}

	public static Profile createProfile() {
		return createProfile("haha", IT);
	}

	public static TagIds createTags(final String... tags) {
		return new TagIds(Arrays.stream(tags).map(Tag::new).collect(toList()));
	}

	public static Profile createProfile(final String username, final Category... categories) {
		return new Profile(username, new FavoriteCategories(Arrays.stream(categories).collect(toList())));
	}

	public static Bookmark createBookmark() {
		return new Bookmark(createProfile(), createLinkMetadata(), "title", "dream company", OpenType.ALL, null,
			"google.com", createTags());
	}

	public static LinkMetadata createLinkMetadata() {
		return new LinkMetadata("google.com", "구글", "google.png");
	}

	public static Pageable createPageable(String... properties) {
		return PageRequest.of(0, 8, Sort.by(properties));
	}

}
