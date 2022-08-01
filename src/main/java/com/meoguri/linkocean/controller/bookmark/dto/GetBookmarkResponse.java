package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarkResult.GetBookmarkProfileResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarkResponse {

	private final String title;
	private final String url;
	private final String imageUrl;
	private final String category;
	private final String memo;
	private final String openType;
	private final boolean isFavorite;
	private final LocalDateTime updatedAt;
	private final List<String> tags;
	private final Map<String, Long> reactionCount;
	private final GetBookmarkProfileResponse profile;

	public static GetBookmarkResponse of(final GetBookmarkResult result) {
		return new GetBookmarkResponse(
			result.getTitle(),
			result.getUrl(),
			result.getImageUrl(),
			result.getCategory(),
			result.getMemo(),
			result.getOpenType(),
			result.isFavorite(),
			result.getUpdatedAt(),
			result.getTags(),
			result.getReactionCount(),
			GetBookmarkProfileResponse.of(result.getProfile())
		);
	}

	@Getter
	@RequiredArgsConstructor
	static class GetBookmarkProfileResponse {

		private final long profileId;
		private final String username;
		private final String imageUrl;
		private final boolean isFollow;

		public static GetBookmarkProfileResponse of(final GetBookmarkProfileResult profile) {
			return new GetBookmarkProfileResponse(
				profile.getProfileId(),
				profile.getUsername(),
				profile.getImageUrl(),
				profile.isFollow()
			);
		}
	}

}
