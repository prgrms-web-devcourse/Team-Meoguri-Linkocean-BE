package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 상세 조회 결과
 */
@Getter
@Builder
public final class GetDetailedBookmarkResult {

	private final String title;
	private final String url;
	private final String image;
	private final String category;
	private final String memo;
	private final String openType;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;

	private List<String> tags;

	private Map<ReactionType, Long> reactionCount;
	private Map<ReactionType, Boolean> reaction;

	private final GetBookmarkProfileResult profile;

	@Getter
	@RequiredArgsConstructor
	public static class GetBookmarkProfileResult {
		private final long profileId;
		private final String username;
		private final String image;
		private final boolean isFollow;
	}
}
