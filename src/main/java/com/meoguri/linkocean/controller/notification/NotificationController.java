package com.meoguri.linkocean.controller.notification;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.NotificationService;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

	private final NotificationService notificationService;

	/* 공유 알림 생성 */
	@PostMapping("/share")
	public void shareNotification(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody Map<String, Long> request
	) {
		notificationService.shareNotification(
			new ShareNotificationCommand(
				user.getProfileId(),
				request.get("targetId"),
				request.get("bookmarkId")
			)
		);
	}

	/* 알림 조회 */
	@GetMapping
	public SliceResponse<Notification> getNotifications(
		final Pageable pageable
	) {
		final Slice<Notification> result = notificationService.getNotifications(pageable);

		return SliceResponse.of("notifications", result.getContent());
	}
}
