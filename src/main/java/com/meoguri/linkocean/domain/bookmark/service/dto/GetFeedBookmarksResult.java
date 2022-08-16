package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 피드 조회 결과
 */
@Getter
@RequiredArgsConstructor
public final class GetFeedBookmarksResult {

	private final long id;
	private final String title;
	private final String url;
	private final OpenType openType;
	private final Category category;
	private final LocalDateTime createdAt;

	private final String image;
	private final long likeCount;
	private final boolean isFavorite;
	private final boolean isWriter;
	private final List<String> tags;
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
