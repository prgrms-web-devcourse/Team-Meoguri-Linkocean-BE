package com.meoguri.linkocean.domain.bookmark.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 북마크의 공개 범위
 */
@Getter
@RequiredArgsConstructor
public enum OpenType {

	/* 전체공개 */
	ALL((byte)10),

	/* 개인 공개 */
	PRIVATE((byte)30);

	/* db 저장용 코드 */
	private final byte code;

	public static String toString(OpenType openType) {
		return openType.name().toLowerCase();
	}

	public static OpenType of(String arg) {
		return OpenType.valueOf(arg.toUpperCase());
	}
}
