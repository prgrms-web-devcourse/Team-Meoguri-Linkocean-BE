package com.meoguri.linkocean.domain.util;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Tag;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.UltimateBookmarkFindCond;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;

public final class Fixture {

	public static User createUser() {
		return createUser("haha@crush.com", "GOOGLE");
	}

	public static User createUser(String email, String oauthType) {
		return new User(email, oauthType);
	}

	public static Profile createProfile() {
		return createProfile(createUser());
	}

	public static Profile createProfile(User user) {
		return createProfile(user, "haha");
	}

	public static Profile createProfile(User user, String username) {
		return new Profile(user, username);
	}

	public static Bookmark createBookmark() {

		return createBookmark(createProfile(), createLinkMetadata(), "인문");
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata) {

		return createBookmark(profile, linkMetadata, "title", "인문");
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String category) {

		return createBookmark(profile, linkMetadata, "title", category);
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, String category) {

		return createBookmark(profile, linkMetadata, title, category, "https://www.google.com",
			Collections.emptyList());
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, String category,
		String url) {
		return createBookmark(profile, linkMetadata, title, category, url,
			Collections.emptyList());
	}

	public static Bookmark createBookmark(Profile profile, LinkMetadata linkMetadata, String title, String category,
		String url, List<Tag> tags) {

		return Bookmark.builder()
			.profile(profile)
			.title(title)
			.linkMetadata(linkMetadata)
			.memo("dream company")
			.openType("all")
			.category(category)
			.url(url)
			.tags(tags)
			.build();
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

	// 검색 조건, 페이지 픽스쳐
	public static UltimateBookmarkFindCond ultimateCategoryFindCond(final long profileId,
		final Bookmark.Category category) {
		return ultimateCategoryFindCond(profileId, category, null);
	}

	public static UltimateBookmarkFindCond ultimateCategoryFindCond(final long profileId,
		final Bookmark.Category category,
		final String title) {
		return new UltimateBookmarkFindCond(0, profileId, category, false, null, false, title);
	}

	public static UltimateBookmarkFindCond ultimateFavoriteFindCond(final long profileId, final String title) {
		return new UltimateBookmarkFindCond(0, profileId, null, true, null, false, title);
	}

	public static UltimateBookmarkFindCond ultimateFavoriteFindCond(final long profileId) {
		return ultimateFavoriteFindCond(profileId, null);
	}

	public static UltimateBookmarkFindCond ultimateTagFindCond(final long profileId, final List<String> tags,
		final String title) {
		return new UltimateBookmarkFindCond(0, profileId, null, false, tags, false, title);
	}

	public static UltimateBookmarkFindCond ultimateTagFindCond(final long profileId, final List<String> tags) {
		return ultimateTagFindCond(profileId, tags, null);
	}

	public static UltimateBookmarkFindCond ultimateFindCond(final long profileId) {
		return ultimateFindCond(profileId, null);
	}

	public static UltimateBookmarkFindCond ultimateFindCond(final long profileId, final String title) {
		return new UltimateBookmarkFindCond(0, profileId, null, false, null, false, title);
	}

	public static PageRequest defaultPageable() {
		return PageRequest.of(0, 8, Sort.by("upload"));
	}

	public static PageRequest likePageable() {
		return PageRequest.of(0, 8, Sort.by("like", "upload"));
	}

}
