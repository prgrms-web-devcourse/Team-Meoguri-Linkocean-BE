package com.meoguri.linkocean.domain.bookmark.entity.vo;

/**
 * 북마크의 공개 범위
 */
public enum OpenType {
	/* 전체공개 */
	ALL,

	/* 팔로워 대상 공개 */
	PARTIAL,

	/* 개인 공개 */
	PRIVATE;

	public static String toString(OpenType openType) {
		return openType.name().toLowerCase();
	}

	public static OpenType of(String arg) {
		return OpenType.valueOf(arg.toUpperCase());
	}
}
