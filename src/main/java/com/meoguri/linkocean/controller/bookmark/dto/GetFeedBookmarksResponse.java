package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetFeedBookmarksResponse {

	private final long id;
	private final String title;
	private final String url;
	private final String openType;
	private final String category;
	private final LocalDateTime updatedAt;

	private final long likeCount;
	private final Boolean isFavorite;
	private final Boolean isWriter;
	private final String imageUrl;
	private final List<String> tags;

	private final GetFeedBookmarkProfileResponse profile;

	@Getter
	@RequiredArgsConstructor
	public static class GetFeedBookmarkProfileResponse {

		private final long profileId;
		private final String username;
		private final String imageUrl;
		private final Boolean isFollow;
	}
}
