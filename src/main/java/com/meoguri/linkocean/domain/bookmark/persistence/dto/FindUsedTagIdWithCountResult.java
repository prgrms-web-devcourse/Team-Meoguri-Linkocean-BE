package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

/**
 * 태그 목록 조회 결과
 * - 개별 태그와 태그별 북마크의 숫자를 반환
 */
@Getter
public final class FindUsedTagIdWithCountResult {

	private final long tagId;
	private final long count;

	@QueryProjection
	public FindUsedTagIdWithCountResult(final long tagId, final long count) {
		this.tagId = tagId;
		this.count = count;
	}
}
