package com.meoguri.linkocean.support.controller;

import lombok.RequiredArgsConstructor;

/**
 * 컨트롤러 계층에서 사용하는 기본 상수값들 모음
 */
@RequiredArgsConstructor
public enum Default {

	LINK_METADATA_IMAGE("default-image.png"),
	LINK_METADATA_TITLE("제목 없음"),
	BOOKMARK_CATEGORY("no-category");

	private final String defaultValue;

	public String getText(String text) {
		return text == null ? defaultValue : text;
	}
}
