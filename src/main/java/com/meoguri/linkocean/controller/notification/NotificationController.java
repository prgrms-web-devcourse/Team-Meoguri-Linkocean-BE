package com.meoguri.linkocean.controller.notification;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.controller.notification.dto.ShareNotificationRequest;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

	private final NotificationService notificationService;

	/* 공유 알림 생성 */
	@PostMapping
	public void shareNotification(
		final @LoginUser SessionUser user,
		final @RequestBody ShareNotificationRequest request
	) {
		notificationService.shareNotification(request.toCommand(user.getId()));
	}

	/* 알림 조회 */
	@GetMapping
	public SliceResponse<Notification> getNotifications(
		final @LoginUser SessionUser user
	) {
		final List<Notification> notifications = notificationService.getNotifications(user.getId());
		return SliceResponse.of("notifications", notifications);
	}
}
