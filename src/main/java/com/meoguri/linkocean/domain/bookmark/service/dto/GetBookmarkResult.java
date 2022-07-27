package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 상세 조회 결과
 */
@Getter
@RequiredArgsConstructor
public class GetBookmarkResult {

	private final long id;
	private final String url;
	private final String title;
	private final String memo;
	private final String openType;
	private final String category;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;
	private List<String> tags;
	private final LinkMetadataResult linkMetadata;
	private Map<String, Integer> reactionCount;
}
