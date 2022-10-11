package com.meoguri.linkocean.internal.bookmark.entity.vo;

/**
 * 북마크 리액션 타입
 */
public enum ReactionType {

	/* 좋아요 👍 */
	LIKE,

	/* 싫어요 👎 */
	HATE;

	public String getName() {
		return name().toLowerCase();
	}

	public static ReactionType of(String arg) {
		return ReactionType.valueOf(arg.toUpperCase());
	}
}
