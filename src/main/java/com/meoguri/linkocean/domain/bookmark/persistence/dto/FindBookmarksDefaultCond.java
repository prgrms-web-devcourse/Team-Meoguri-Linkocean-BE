package com.meoguri.linkocean.domain.bookmark.persistence.dto;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class FindBookmarksDefaultCond {

	private final int page;
	private final int size;

	private final String order;
	private final long profileId;
	private final String searchTitle;
	private List<OpenType> openTypes = Arrays.stream(OpenType.values()).collect(Collectors.toList());

	public void changeOpenType(List<OpenType> openTypes) {
		this.openTypes = openTypes;
	}

	public int getOffset() {
		return (page - 1) * size;
	}

	public int getLimit() {
		return size;
	}
}
