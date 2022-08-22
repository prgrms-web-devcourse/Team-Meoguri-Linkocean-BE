package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType.*;
import static com.meoguri.linkocean.domain.notification.entity.vo.NotificationType.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class NotificationServiceImplTest extends BaseServiceTest {

	@Autowired
	private NotificationService notificationService;

	private long senderProfileId;
	private long receiver1ProfileId;
	private long receiver2ProfileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		senderProfileId = 사용자_프로필_동시_등록("sender@gamil.com", GOOGLE, "sender", IT);
		receiver1ProfileId = 사용자_프로필_동시_등록("receiver1@gamil.com", GOOGLE, "receiver1", IT);
		receiver2ProfileId = 사용자_프로필_동시_등록("receiver2@gamil.com", GOOGLE, "receiver2", IT);

		bookmarkId = 북마크_등록(senderProfileId, "www.google.com", "구글", "메모", IT, ALL);

		팔로우(receiver1ProfileId, senderProfileId);
		팔로우(receiver2ProfileId, senderProfileId);
	}

	@Test
	void 북마크_공유_조회_성공() {
		//given
		북마크_공유(senderProfileId, receiver1ProfileId, bookmarkId);

		//when
		final Slice<Notification> result = notificationService.getNotifications(createPageable(), receiver1ProfileId);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getType()).isEqualTo(SHARE);
		assertThat(result.getContent().get(0).getReceiver().getId()).isEqualTo(receiver1ProfileId);
		assertThat(result.getContent().get(0).getInfo()).containsAllEntriesOf(Map.of(
			"bookmark", Map.of(
				"id", bookmarkId,
				"title", "구글",
				"link", "www.google.com"
			),
			"sender", Map.of(
				"id", senderProfileId,
				"username", "sender"
			)
		));
	}

	@Test
	void 북마크_공유_조회_성공_자기_알림만_조회() {
		//given
		북마크_공유(senderProfileId, receiver1ProfileId, bookmarkId);
		북마크_공유(senderProfileId, receiver2ProfileId, bookmarkId);

		//when
		final Slice<Notification> result = notificationService.getNotifications(createPageable(), receiver1ProfileId);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getType()).isEqualTo(SHARE);
		assertThat(result.getContent().get(0).getReceiver().getId()).isEqualTo(receiver1ProfileId);
		assertThat(result.getContent().get(0).getInfo()).containsAllEntriesOf(Map.of(
			"bookmark", Map.of(
				"id", bookmarkId,
				"title", "구글",
				"link", "www.google.com"
			),
			"sender", Map.of(
				"id", senderProfileId,
				"username", "sender"
			)
		));
	}
}
