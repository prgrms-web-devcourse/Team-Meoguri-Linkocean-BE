package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.notification.entity.vo.NotificationType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class NotificationServiceImplTest {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private FollowRepository followRepository;

	private Profile senderProfile;
	private Profile receiver1Profile;
	private Profile receiver2Profile;
	private long senderProfileId;
	private long receiver1ProfileId;
	private long receiver2ProfileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 링크 메타 데이터 셋업
		User sender = userRepository.save(new User("sender@gmail.com", "GOOGLE"));
		User receiver1 = userRepository.save(new User("receiver1@gmail.com", "GOOGLE"));
		User receiver2 = userRepository.save(new User("receiver2@gmail.com", "GOOGLE"));

		senderProfile = profileRepository.save(new Profile(sender, "sender"));
		receiver1Profile = profileRepository.save(new Profile(receiver1, "receiver1"));
		receiver2Profile = profileRepository.save(new Profile(receiver2, "receiver2"));

		senderProfileId = senderProfile.getId();
		receiver1ProfileId = receiver1Profile.getId();
		receiver2ProfileId = receiver2Profile.getId();

		final LinkMetadata linkMetadata = linkMetadataRepository.save(createLinkMetadata());

		final Bookmark bookmark = createBookmark(senderProfile, linkMetadata, "title", IT, "www.naver.com");
		bookmarkId = bookmarkRepository.save(bookmark).getId();

		followRepository.save(new Follow(receiver1Profile, senderProfile));
		followRepository.save(new Follow(receiver2Profile, senderProfile));
	}

	@Test
	void 북마크_공유_조회_성공() {
		//given
		final ShareNotificationCommand command =
			new ShareNotificationCommand(senderProfileId, receiver1ProfileId, bookmarkId);
		notificationService.shareNotification(command);

		//when
		final Slice<Notification> result = notificationService.getNotifications(defaultPageable(), receiver1ProfileId);

		//then
		assertThat(result).hasSize(1);
		final Notification notification = result.getContent().get(0);
		assertThat(notification.getType()).isEqualTo(SHARE);
		assertThat(notification.getReceiver().getId()).isEqualTo(receiver1Profile.getId());
		assertThat(notification.getInfo()).containsAllEntriesOf(Map.of(
			"bookmark", Map.of(
				"id", bookmarkId,
				"title", "title",
				"link", "www.naver.com"
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
		final ShareNotificationCommand command1 =
			new ShareNotificationCommand(senderProfileId, receiver1ProfileId, bookmarkId);
		notificationService.shareNotification(command1);

		final ShareNotificationCommand command2 =
			new ShareNotificationCommand(senderProfileId, receiver2ProfileId, bookmarkId);
		notificationService.shareNotification(command2);

		//when
		final Slice<Notification> result = notificationService.getNotifications(defaultPageable(), receiver1ProfileId);

		//then
		assertThat(result).hasSize(1);
		final Notification notification = result.getContent().get(0);
		assertThat(notification.getType()).isEqualTo(SHARE);
		assertThat(notification.getReceiver().getId()).isEqualTo(receiver1Profile.getId());
		assertThat(notification.getInfo()).containsAllEntriesOf(Map.of(
			"bookmark", Map.of(
				"id", bookmarkId,
				"title", "title",
				"link", "www.naver.com"
			),
			"sender", Map.of(
				"id", senderProfileId,
				"username", "sender"
			)
		));
	}
}
