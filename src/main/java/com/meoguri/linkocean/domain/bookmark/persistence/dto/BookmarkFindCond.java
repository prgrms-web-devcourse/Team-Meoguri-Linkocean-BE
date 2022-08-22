package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 북마크 조회 조건
 */
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public final class BookmarkFindCond {

	/* 현재 사용자의 프로필 id */
	private final long currentUserProfileId;

	/*대상을 지정한 북마크 조회인 경우 대상 프로필 id
	 만약 피드 페이지 조회 등의 작성자가 다양한 조회의 경우 null*/
	private final Long targetProfileId;

	/* 카테고리 필터링 조회인 경우 카테고리 아니라면 null */
	private final Category category;

	/* 즐겨찾기 필터링 조회인 경우 true 아니라면 null */
	private final Boolean favorite;

	/* 태그 필터링 조회인 경우 태그 목록 아니라면 null */
	private final List<String> tags;

	/* 현재 사용자가 팔로우 하는 사용자대상 북마크의 필터링 여부
	   피드 북마크 조회에 대해서만 참이 될 수 있다 아니라면 null*/
	private final Boolean follow;

	/* 제목 검색 조건 - contains 로 판별한다 */
	private final String title;

	/* 공개 범위 조건 - 북마크 작성자와 자신의 관계에 따라 결정 된다
	   조건은 계층적이다
	   현재 사용자가 북마크 조회 대상 사용자 인 경우 PRIVATE 		-> PRIVATE, PARTIAL, ALL 모든 북마크에 접근 가능
	   현재 사용자가 북마크 조회 대상을 팔로우 중인 경우 PARTIAL 	-> PARTIAL, ALL 북마크에 접근 가능
	   그 외의 경우 ALL								 	-> ALL 북마크만 접근 가능 .*/
	@Setter
	private OpenType openType;
}
