package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * 북마크 상세 조회 결과
 */
@Getter
@Builder
public class GetBookmarkResult {

	private final String title;
	private final String url;
	private final String imageUrl;
	private final String category;
	private final String memo;
	private final String openType;
	private final boolean isFavorite;
	private final LocalDateTime updatedAt;

	private List<String> tags;

	private Map<String, Long> reactionCount;

	//Object ? (더 좋은 방법은 없을까?)
	private Map<String, Object> profile;
}
