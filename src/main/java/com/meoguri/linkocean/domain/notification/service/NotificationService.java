package com.meoguri.linkocean.domain.notification.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

public interface NotificationService {

	/**
	 * 북마크 공유 알림 생성
	 * - 나를 팔로우 해주는 상대에게만 공유 할 수 있다.
	 * - 전체 공유 북마크만 공유할 수 있다.
	 */
	void shareNotification(ShareNotificationCommand command);

	Slice<Notification> getNotifications(Pageable pageable, long profileId);
}
