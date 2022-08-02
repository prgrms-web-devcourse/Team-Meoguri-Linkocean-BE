package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkQueryDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 목록 조회 결과
 */
@Getter
@RequiredArgsConstructor
public final class GetBookmarksResult {

	private final long id;
	private final String url;
	private final String title;
	private final String openType;
	private final String category;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;
	private final long likeCount;
	private final String imageUrl;
	private final List<String> tags;

	public static GetBookmarksResult of(BookmarkQueryDto bookmarkQueryDto) {
		return new GetBookmarksResult(
			bookmarkQueryDto.getId(),
			bookmarkQueryDto.getUrl(),
			bookmarkQueryDto.getTitle(),
			bookmarkQueryDto.getOpenType(),
			bookmarkQueryDto.getCategory(),
			bookmarkQueryDto.getUpdatedAt(),
			bookmarkQueryDto.isFavorite(),
			bookmarkQueryDto.getLikeCount(),
			bookmarkQueryDto.getImageUrl(),
			bookmarkQueryDto.getTagNames());
	}
}
