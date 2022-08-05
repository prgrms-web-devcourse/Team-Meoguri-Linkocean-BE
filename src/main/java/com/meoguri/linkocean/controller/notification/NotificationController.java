package com.meoguri.linkocean.controller.notification;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
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
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody ShareNotificationRequest request
	) {
		notificationService.shareNotification(request.toCommand(user.getId()));
	}

	/* 알림 조회 */
	// 개발 속도 및 적절한 방법을 모르는 고려하여 그냥 Entity 를 반환함 추후 학습하여 리팩토링
	@GetMapping
	public SliceResponse<Notification> getNotifications(
		final @AuthenticationPrincipal SecurityUser user
	) {
		final List<Notification> notifications = notificationService.getNotifications(user.getId());
		return SliceResponse.of("notifications", notifications);
	}
}
