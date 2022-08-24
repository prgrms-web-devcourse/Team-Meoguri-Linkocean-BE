package com.meoguri.linkocean.domain.profile.service.query.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 태그 목록 조회 결과
 * - 개별 태그와 태그별 북마크의 숫자를 반환
 */
@Getter
@RequiredArgsConstructor
public final class GetProfileTagsResult {

	private final String tag;
	private final int count;
}
