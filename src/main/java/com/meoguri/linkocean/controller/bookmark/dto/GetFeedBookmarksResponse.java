package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetFeedBookmarksResult;

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
	private final ProfileResponse profile;

	@Getter
	@RequiredArgsConstructor
	public static class ProfileResponse {

		private final long profileId;
		private final String username;
		private final String imageUrl;
		private final Boolean isFollow;
	}

	public static GetFeedBookmarksResponse of(GetFeedBookmarksResult result) {
		final GetFeedBookmarksResult.ProfileResult profileResult = result.getProfile();

		final String openType = OpenType.toString(result.getOpenType());
		final String category = Optional.ofNullable(Category.toStringKor(result.getCategory())).orElse("no-category");

		return new GetFeedBookmarksResponse(
			result.getId(),
			result.getTitle(),
			result.getUrl(),
			openType,
			category,
			result.getUpdatedAt(),
			result.getLikeCount(),
			result.isFavorite(),
			result.isFavorite(),
			result.getImage(),
			result.getTags(),
			new ProfileResponse(
				profileResult.getProfileId(),
				profileResult.getUsername(),
				profileResult.getImage(),
				profileResult.isFollow()
			)
		);
	}
}
