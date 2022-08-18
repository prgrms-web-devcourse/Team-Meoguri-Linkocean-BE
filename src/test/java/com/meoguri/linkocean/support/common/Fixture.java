package com.meoguri.linkocean.support.common;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

public final class Fixture {

	public static User createUser() {
		return createUser("haha@crush.com", OAuthType.GOOGLE);
	}

	public static User createUser(String email, OAuthType oauthType) {
		return new User(new Email(email), oauthType);
	}

	public static Profile createProfile(final String username, final Category... categories) {
		return new Profile(username, Arrays.stream(categories).collect(toList()));
	}

	@Deprecated
	public static Profile createProfile() {
		return createProfile(createUser());
	}

	@Deprecated
	public static Profile createProfile(User user) {
		return createProfile(user, "haha");
	}

	@Deprecated
	public static Profile createProfile(User user, String username) {
		return new Profile(user, username);
	}

	public static Bookmark createBookmark() {

		return createBookmark(createProfile(), createLinkMetadata(), "google.com");
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata) {

		return createBookmark(profile, linkMetadata, "title", null);
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, Category category) {

		return createBookmark(profile, linkMetadata, "title", category);
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, Category category) {

		return createBookmark(profile, linkMetadata, title, category, "https://www.google.com", emptyList());
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, Category category,
		String url) {
		return createBookmark(profile, linkMetadata, title, category, url, emptyList());
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String url) {
		return createBookmark(profile, linkMetadata, "title", null, url, emptyList());
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, Category category,
		String url, List<Tag> tags) {

		return new Bookmark(profile, linkMetadata, title, "dream company", OpenType.ALL, category, url, tags);
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, OpenType openType, String url) {

		return new Bookmark(profile, linkMetadata, null, null, openType, null, url, emptyList());
	}

	public static LinkMetadata createLinkMetadata() {

		return createLinkMetadata("google.com");
	}

	public static LinkMetadata createLinkMetadata(String link) {

		return new LinkMetadata(

			link, "구글", "google.png");
	}

	public static Tag createTag() {

		return new Tag("tag");
	}

	public static Pageable defaultPageable() {
		return PageRequest.of(0, 8);
	}

	public static Pageable defaultPageableSortByUpload() {
		return PageRequest.of(0, 8, Sort.by("upload"));
	}

	public static Pageable defaultPageableSortByLike() {
		return PageRequest.of(0, 8, Sort.by("like", "upload"));
	}

}
