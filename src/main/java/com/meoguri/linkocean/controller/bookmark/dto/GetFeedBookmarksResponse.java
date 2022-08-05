package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty("imageUrl")
	private final String image;
	private final List<String> tags;

	private final GetFeedBookmarkProfileResponse profile;

	@Getter
	@RequiredArgsConstructor
	public static class GetFeedBookmarkProfileResponse {

		private final long profileId;
		private final String username;

		@JsonProperty("imageUrl")
		private final String image;
		private final Boolean isFollow;
	}
}
