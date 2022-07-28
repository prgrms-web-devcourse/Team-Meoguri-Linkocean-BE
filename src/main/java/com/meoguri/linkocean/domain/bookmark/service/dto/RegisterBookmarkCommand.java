package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterBookmarkCommand {

	private final String url;
	private final String title;
	private final String memo;
	private final String category;
	private final List<String> tags;
	private final String openType;
}
