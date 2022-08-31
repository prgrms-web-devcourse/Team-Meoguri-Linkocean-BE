package com.meoguri.linkocean.test.support.domain.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.TagIds;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.command.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.command.entity.Profile;

@EntityTest
public abstract class BaseEntityTest {

	protected static Profile createProfile() {
		return new Profile("haha", new FavoriteCategories(Arrays.stream(new Category[] {IT}).collect(toList())));
	}

	protected static TagIds createTagIds(final long... tags) {
		return new TagIds(Arrays.stream(tags).boxed().collect(toList()));
	}

	protected static Bookmark createBookmark() {
		return new Bookmark(createProfile(), createLinkMetadata().getId(), "title", "dream company", OpenType.ALL, null,
			"google.com", createTagIds());
	}

	protected static LinkMetadata createLinkMetadata() {
		return new LinkMetadata("google.com", "구글", "google.png");
	}
}
