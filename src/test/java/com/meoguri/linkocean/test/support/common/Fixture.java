package com.meoguri.linkocean.test.support.common;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;
import com.meoguri.linkocean.domain.tag.entity.Tag;
import com.meoguri.linkocean.domain.tag.entity.Tags;

public final class Fixture {

	public static Profile createProfile() {
		return new Profile("haha", new FavoriteCategories(Arrays.stream(new Category[] {IT}).collect(toList())));
	}

	public static Tags createTags(final String... tags) {
		return new Tags(Arrays.stream(tags).map(Tag::new).collect(toList()));
	}

	public static Bookmark createBookmark() {
		return new Bookmark(createProfile(), createLinkMetadata(), "title", "dream company", OpenType.ALL, null,
			"google.com", createTags());
	}

	public static LinkMetadata createLinkMetadata() {
		return new LinkMetadata("google.com", "구글", "google.png");
	}

}
