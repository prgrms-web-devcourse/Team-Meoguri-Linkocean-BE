package com.meoguri.linkocean.controller.bookmark.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
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
	private final LocalDateTime createdAt;

	private final long likeCount;
	private final Boolean isFavorite;
	private final Boolean isWriter;
	private final String imageUrl;
	private final List<String> tags;

	public static GetBookmarksResponse of(GetBookmarksResult result) {

		final String openType = OpenType.toString(result.getOpenType());
		final String category = Optional.ofNullable(Category.toStringKor(result.getCategory())).orElse("no-category");

		return new GetBookmarksResponse(
			result.getId(),
			result.getTitle(),
			result.getUrl(),
			openType,
			category,
			result.getCreatedAt(),
			result.getLikeCount(),
			result.isFavorite(),
			result.isWriter(),
			result.getImage(),
			result.getTags()
		);
	}
}
