package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 내 북마크 목록 조회 결과
 */
@Getter
@RequiredArgsConstructor
public final class GetMyBookmarksResult {

	private final long id;
	private final String url;
	private final String title;
	private final String openType;
	private final String category;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;
	private int likeCount;
	private List<String> tags;
	private final LinkMetadataResult linkMetadata;
}
