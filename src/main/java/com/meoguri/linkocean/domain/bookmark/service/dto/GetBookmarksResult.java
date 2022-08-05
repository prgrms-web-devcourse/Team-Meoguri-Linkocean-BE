package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;

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
	private final String image;
	private final boolean isWriter;
	private final List<String> tags;
}
