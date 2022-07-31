package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 태그 목록 조회 결과
 * - 개별 태그와 태그별 북마크의 숫자를 반환
 */
@Getter
@RequiredArgsConstructor
public class GetMyTagsResult {

	private final String tag;
	private final int count;
}
