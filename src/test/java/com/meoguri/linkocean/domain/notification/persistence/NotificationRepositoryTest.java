package com.meoguri.linkocean.domain.notification.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.meoguri.linkocean.domain.notification.entity.Noti;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.entity.NotificationType;
import com.meoguri.linkocean.domain.notification.entity.noti.BookmarkNoti;
import com.meoguri.linkocean.domain.notification.entity.noti.ProfileNoti;

@DataMongoTest
class NotificationRepositoryTest {

	@Autowired
	private NotificationRepository repository;

	@Test
	void 공유알림_하나_저장하고_조회_성공() {

		//given
		final long targetProfileId = 1L;
		final Map<String, Noti> info = Map.of(
			"sender", new ProfileNoti(2L, "haha"),
			"bookmark", new BookmarkNoti(3L, "네이버", "https://www.naver.com"));

		final Notification notification =
			repository.save(new Notification(NotificationType.SHARE, targetProfileId, info));

		//when
		final List<Notification> findNotifications = repository.findByTargetProfileId(targetProfileId);

		//then
		assertThat(findNotifications).hasSize(1)
			.containsExactly(notification);
	}
}
