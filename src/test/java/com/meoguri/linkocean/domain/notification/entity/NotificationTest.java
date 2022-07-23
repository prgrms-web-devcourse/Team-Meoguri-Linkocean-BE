package com.meoguri.linkocean.domain.notification.entity;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;

class NotificationTest {

	@Test
	void 북마크_공유_알림_생성_성공() {
		//given
		final Bookmark bookmark = createBookmark();
		final Profile profile = createProfile();

		//when
		final Notification notification = new Notification(bookmark, profile);

		//then
		assertThat(notification).isNotNull()
			.extracting(Notification::getBookmark, Notification::getTarget)
			.containsExactly(bookmark, profile);
		assertThat(notification.getCreatedAt()).isNotNull();
	}
}