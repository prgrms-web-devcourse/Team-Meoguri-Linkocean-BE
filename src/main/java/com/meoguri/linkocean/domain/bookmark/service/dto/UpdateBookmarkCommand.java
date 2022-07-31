package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UpdateBookmarkCommand {

	private final long userId;
	private final long bookmarkId;
	private final String title;
	private final String memo;
	private final String category;
	private final List<String> tagNames;
	private final String openType;
}
