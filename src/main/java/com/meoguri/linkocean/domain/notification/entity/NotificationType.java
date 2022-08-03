package com.meoguri.linkocean.domain.notification.entity;

public enum NotificationType {

	/* 공유 알림
	알림 유발자의 ProfileNoti 와 공유 대상 북마크의 BookmarkNoti 추가 정보 필요 */
	SHARE,

	/* 팔로우 한 사람의 업로드 알림
	알림 유발자의 ProfileNoti 와 공유 대상 북마크의 BookmarkNoti 추가 정보 필요 */
	FEED,

	/* 확인한지 오래된 북마크 알림
	해당 북마크의 BookmarkNoti 추가 정보 필요 */
	OLD
}
