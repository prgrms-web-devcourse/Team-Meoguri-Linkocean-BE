package com.meoguri.linkocean.domain.notification.service;

import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

public interface NotificationService {

	void shareNotification(ShareNotificationCommand command);
}
