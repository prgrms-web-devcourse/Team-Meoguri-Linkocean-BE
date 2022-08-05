package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarksResponse {

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

	public static GetBookmarksResponse of(GetBookmarksResult result) {

		return new GetBookmarksResponse(
			result.getId(),
			result.getTitle(),
			result.getUrl(),
			result.getOpenType(),
			result.getCategory(),
			result.getUpdatedAt(),
			result.getLikeCount(),
			result.isFavorite(),
			result.isWriter(),
			result.getImage(),
			result.getTags()
		);
	}
}
