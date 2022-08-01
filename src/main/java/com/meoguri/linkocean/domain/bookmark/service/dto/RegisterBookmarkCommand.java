package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RegisterBookmarkCommand {

	private final long userId;
	private final String url;
	private final String title;
	private final String memo;
	private final String category;
	private final String openType;
	private final List<String> tagNames;
}
