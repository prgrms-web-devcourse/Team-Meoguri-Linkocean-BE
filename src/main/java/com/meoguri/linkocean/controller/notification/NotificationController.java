package com.meoguri.linkocean.controller.notification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.internal.notification.entity.Notification;
import com.meoguri.linkocean.internal.notification.service.NotificationService;
import com.meoguri.linkocean.support.controller.dto.SliceResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

	private final NotificationService notificationService;

	/* 알림 목록 조회 */
	@GetMapping
	public SliceResponse<Notification> getNotifications(
		final @AuthenticationPrincipal SecurityUser user,
		final Pageable pageable
	) {
		final Slice<Notification> result = notificationService.getNotifications(pageable, user.getProfileId());

		return SliceResponse.of("notifications", result.getContent(), result.hasNext());
	}
}
