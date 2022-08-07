package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 북마크 조회 조건 */
@Getter
@RequiredArgsConstructor
public final class BookmarkFindCond {

	private final long profileId;
	private final String searchTitle;
}
