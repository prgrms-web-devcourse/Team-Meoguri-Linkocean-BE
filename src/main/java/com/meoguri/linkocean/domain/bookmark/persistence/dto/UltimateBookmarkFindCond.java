package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * 궁극의 북마크 조회 조건
 * - `BookmarkFindCond`,
 * - `FeedBookmarksSearchCond`,
 * - `MyBookmarkSearchCond`,
 * - `OtherBookmarksSearchCond` 를 통합 할 조건이다
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public final class UltimateBookmarkFindCond {

	/* 현재 사용자의 프로필 id */
	private final long currentUserProfileId;

	/*작성자를 지정한 북마크 조회인 경우 대상 프로필 id
	 만약 피드 페이지 조회 등의 작성자가 다양한 조회의 경우 null*/
	private final Long targetProfileId;

	/* 카테고리 필터링 조회인 경우 카테고리 아니라면 null */
	private final Bookmark.Category category;

	/* 즐겨찾기 필터링 조회인 경우 true 아니라면 false */
	private final boolean favorite;

	/* 태그 필터링 조회인 경우 태그 목록 아니라면 null */
	private final List<String> tags;

	/* 현재 사용자가 팔로우 하는 사용자대상 북마크의 필터링 여부
	   피드 북마크 조회에 대해서만 참이 될 수 있다 */
	private final boolean follow;

	/* 제목 검색 조건 - contains 로 판별한다 */
	private final String title;
}
