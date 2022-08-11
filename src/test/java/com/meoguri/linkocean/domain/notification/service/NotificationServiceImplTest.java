package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.notification.entity.NotificationType.*;
import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.TestPropertySource;

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

@TestPropertySource(properties = {
	"spring.flyway.enabled=true",
	"spring.flyway.locations=classpath:db/tests"
})
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
	private Profile receiverProfile;
	private long senderProfileId;
	private long receiverProfileId;
	private long bookmarkId;

	@BeforeEach
	void setUp() {
		// 사용자, 프로필, 링크 메타 데이터 셋업
		User sender = userRepository.save(new User("sender@gmail.com", "GOOGLE"));
		User receiver = userRepository.save(new User("receiver@gmail.com", "GOOGLE"));

		senderProfile = profileRepository.save(new Profile(sender, "sender"));
		receiverProfile = profileRepository.save(new Profile(receiver, "receiver"));

		senderProfileId = senderProfile.getId();
		receiverProfileId = receiverProfile.getId();

		final LinkMetadata linkMetadata = linkMetadataRepository.save(createLinkMetadata());

		final Bookmark bookmark = createBookmark(senderProfile, linkMetadata, "title", IT, "www.naver.com");
		bookmarkId = bookmarkRepository.save(bookmark).getId();

		followRepository.save(new Follow(receiverProfile, senderProfile));
	}

	@Test
	void 북마크_공유_조회_성공() {
		//given
		final ShareNotificationCommand command =
			new ShareNotificationCommand(senderProfileId, receiverProfileId, bookmarkId);

		//when
		notificationService.shareNotification(command);
		final Slice<Notification> result = notificationService.getNotifications(PageRequest.of(0, 8));

		//then
		assertThat(result).hasSize(1);
		final Notification notification = result.getContent().get(0);
		assertThat(notification.getType()).isEqualTo(SHARE);
		assertThat(notification.getReceiver().getId()).isEqualTo(receiverProfile.getId());
		assertThat(notification.getInfo()).containsAllEntriesOf(Map.of(
			"bookmark", Map.of(
				"id", (int)bookmarkId,
				"title", "title",
				"link", "www.naver.com"
			),
			"sender", Map.of(
				"id", (int)senderProfileId,
				"username", "sender"
			)
		));
	}
}
