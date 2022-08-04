package com.meoguri.linkocean.domain.notification.service;

import java.util.List;

import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

public interface NotificationService {

	void shareNotification(ShareNotificationCommand command);

	List<Notification> getNotifications(long userId);
}
