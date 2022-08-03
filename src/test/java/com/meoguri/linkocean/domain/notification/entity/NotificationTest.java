package com.meoguri.linkocean.domain.notification.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.notification.entity.noti.BookmarkNoti;
import com.meoguri.linkocean.domain.notification.entity.noti.ProfileNoti;

class NotificationTest {

	@Test
	void 공유_알림_생성() {
		//given
		final NotificationType type = NotificationType.SHARE;
		final long targetProfileId = 1L;
		final Map<String, Noti> info = Map.of(
			"sender", new ProfileNoti(2L, "haha"),
			"bookmark", new BookmarkNoti(3L, "네이버", "https://www.naver.com")
		);

		//when
		final Notification notification = new Notification(type, targetProfileId, info);

		//then
		assertThat(notification).isNotNull()
			.extracting(Notification::getType, Notification::getTargetProfileId, Notification::getInfo)
			.containsExactly(type, targetProfileId, info);
	}
}
