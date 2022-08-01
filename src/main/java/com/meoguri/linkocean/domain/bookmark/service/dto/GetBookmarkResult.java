package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 상세 조회 결과
 */
@Getter
@Builder
public final class GetBookmarkResult {

	private final String title;
	private final String url;
	private final String imageUrl;
	private final String category;
	private final String memo;
	private final String openType;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;

	private List<String> tags;

	private Map<String, Long> reactionCount;

	private final GetBookmarkProfileResult profile;

	@Getter
	@RequiredArgsConstructor
	public static class GetBookmarkProfileResult {
		private final long profileId;
		private final String username;
		private final String imageUrl;
		private final boolean isFollow;
	}
}
