package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetBookmarksResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarksResponse {

	private final long id;
	private final String title;
	private final String url;
	private final LocalDateTime updatedAt;
	private final String openType;
	private final String category;
	private final int likeCount;
	private final boolean isFavorite;
	private final List<String> tags;

	public static GetBookmarksResponse of(GetBookmarksResult result) {

		return new GetBookmarksResponse(
			result.getId(),
			result.getTitle(),
			result.getUrl(),
			result.getUpdatedAt(),
			result.getOpenType(),
			result.getCategory(),
			result.getLikeCount(),
			result.isFavorite(),
			result.getTags()
		);
	}
}
