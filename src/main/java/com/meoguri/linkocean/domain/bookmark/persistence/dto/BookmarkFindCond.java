package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 북마크 조회 조건 */
@Getter
@RequiredArgsConstructor
public final class BookmarkFindCond {

	private final long profileId;
	private final String searchTitle;
	private List<OpenType> openTypes = Arrays.stream(OpenType.values()).collect(Collectors.toList());

	/* Question - 검색 조건에 왜 세터형 로직이 등장하는지 ?!?!?!? */
	public void changeOpenType(List<OpenType> openTypes) {
		this.openTypes = openTypes;
	}

}
