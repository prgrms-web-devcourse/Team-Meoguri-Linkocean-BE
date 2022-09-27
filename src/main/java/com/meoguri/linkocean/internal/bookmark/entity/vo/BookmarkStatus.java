package com.meoguri.linkocean.internal.bookmark.entity.vo;

/**
 * 북마크 상태
 * 생성시 `등록` 상태를 가지며 북마크 삭제를 통해 `제거` 상태가 된다.
 */
public enum BookmarkStatus {
	REGISTERED,
	REMOVED
}
