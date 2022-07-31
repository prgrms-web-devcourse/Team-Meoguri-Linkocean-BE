package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 피드 조회 결과
 */
@Getter
@RequiredArgsConstructor
public final class GetFeedBookmarksResult {

	private final long id;
	private final String url;
	private final String title;
	private final String memo;
	private final String openType;
	private final String category;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private final boolean isFavorite;
	private final int likeCount;
	private List<String> tags;
	private final LinkMetadataResult linkMetadata;
	private ProfileResult profile;

	@Getter
	@RequiredArgsConstructor
	public static class ProfileResult {

		private final long id;
		private final String username;
		private final String imageUrl;
	}
}
