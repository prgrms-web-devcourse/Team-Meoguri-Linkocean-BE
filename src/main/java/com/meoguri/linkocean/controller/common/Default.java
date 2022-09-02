package com.meoguri.linkocean.controller.common;

/**
 * 컨트롤러 계층에서 사용하는 기본 상수값들 모음
 */
public enum Default {
	IMAGE("default-image.png"),
	TITLE("제목 없음");

	private final String text;

	Default(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
