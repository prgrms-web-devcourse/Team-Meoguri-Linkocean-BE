package com.meoguri.linkocean.controller.notification;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.ListResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

	// private final NotificationService notificationService;

	/* 공유 알림 생성 */
	@PostMapping
	public void shareNotification(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestBody Map<String, Long> request
	) {
		request.get("targetId");
		request.get("bookmarkId");
	}

	/* 알림 조회 */
	@GetMapping
	public ListResponse<Map<String, Object>> getNotifications(
		final @AuthenticationPrincipal SecurityUser user
	) {
		return ListResponse.of("notifications", dummyNotifications());
	}

	private List<Map<String, Object>> dummyNotifications() {
		final Map<String, Object> share = Map.of(
			"type", "SHARE",
			"info", Map.of(
				"bookmark", Map.of(
					"id", 1,
					"title", "네이버",
					"link", "https://www.naver.com"),
				"sender", Map.of(
					"id", 1,
					"username", "haha"
				)
			)
		);
		final Map<String, Object> feed = Map.of(
			"type", "FEED",
			"info", Map.of(
				"bookmark", Map.of(
					"id", 2,
					"title", "구글",
					"link", "https://www.google.com"),
				"sender", Map.of(
					"id", 2,
					"username", "jacob"
				)
			)
		);
		final Map<String, Object> old = Map.of(
			"type", "OLD",
			"info", Map.of(
				"bookmark", Map.of(
					"id", 3,
					"title", "프로그래머스",
					"link", "https://school.programmers.co.kr/my-courses/learning")
			)
		);

		return List.of(share, feed, old);
	}
}
