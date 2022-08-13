package com.meoguri.linkocean.controller.notification;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.NotificationService;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// @RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

	private final NotificationService notificationService;

	/* 공유 알림 생성 */
	@PostMapping("/api/v1/bookmark/{bookmarkId}/share")
	public void shareNotification(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody Map<String, Long> request,
		final @PathVariable Long bookmarkId
	) {
		notificationService.shareNotification(
			new ShareNotificationCommand(
				user.getProfileId(),
				request.get("targetId"),
				bookmarkId
			)
		);
	}

	/* 알림 조회 */
	@GetMapping("/api/v1/notifications")
	public SliceResponse<Notification> getNotifications(
		final @AuthenticationPrincipal SecurityUser user,
		final Pageable pageable
	) {
		final Slice<Notification> result = notificationService.getNotifications(pageable, user.getProfileId());

		return SliceResponse.of("notifications", result.getContent(), result.hasNext());
	}
}
