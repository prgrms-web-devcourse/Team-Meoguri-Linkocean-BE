package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetFeedBookmarksResponse {

	private final String title;
	private final String url;
	private final String imageUrl;
	private final String category;
	private final boolean isFavorite;
	private final LocalDateTime updatedAt;
	private final List<String> tags;
	private final int linkCount;
	private final GetFeedBookmarkProfileResponse profile;

	public static GetFeedBookmarksResponse of(final GetFeedBookmarksResult result) {
		return new GetFeedBookmarksResponse(
			result.getTitle(),
			result.getUrl(),
			result.getImageUrl(),
			result.getCategory(),
			result.isFavorite(),
			result.getUpdatedAt(),
			result.getTags(),
			result.getLikeCount(),
			GetFeedBookmarkProfileResponse.of(result.getProfile())
		);
	}

	@Getter
	@RequiredArgsConstructor
	static class GetFeedBookmarkProfileResponse {

		private final long profileId;
		private final String username;
		private final String imageUrl;

		public static GetFeedBookmarkProfileResponse of(
			final GetFeedBookmarksResult.ProfileResult profile) {
			return new GetFeedBookmarkProfileResponse(
				profile.getId(),
				profile.getUsername(),
				profile.getImageUrl()
			);
		}
	}

}