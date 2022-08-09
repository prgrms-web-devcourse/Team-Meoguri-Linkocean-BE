package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 상세 조회 결과
 */
@Getter
@RequiredArgsConstructor
public final class GetDetailedBookmarkResult {

	private final long bookmarkId;
	private final String title;
	private final String url;
	private final String image;
	private final Category category;
	private final String memo;
	private final OpenType openType;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;
	private final List<String> tags;
	private final Map<ReactionType, Long> reactionCount;
	private final Map<ReactionType, Boolean> reaction;
	private final ProfileResult profile;

	@Getter
	@RequiredArgsConstructor
	public static class ProfileResult {
		private final long profileId;
		private final String username;
		private final String image;
		private final boolean isFollow;
	}
}
