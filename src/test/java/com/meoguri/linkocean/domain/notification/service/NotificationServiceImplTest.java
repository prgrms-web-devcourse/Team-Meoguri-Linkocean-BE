package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.notification.entity.Noti;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.entity.NotificationType;
import com.meoguri.linkocean.domain.notification.entity.noti.BookmarkNoti;
import com.meoguri.linkocean.domain.notification.entity.noti.ProfileNoti;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Follow;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FollowRepository;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;
import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

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

	private User sender;
	private User target;

	private Profile senderProfile;
	private Profile targetProfile;

	private Bookmark notSharableBookmark;
	private Bookmark shareBookmark;

	@BeforeEach
	void setUp() {
		// 유저, 프로필, 링크 셋업
		sender = userRepository.save(new User("sender@gmail.com", "GOOGLE"));
		senderProfile = profileRepository.save(new Profile(sender, "sender"));

		// 유저, 프로필, 링크 셋업
		target = userRepository.save(new User("target@gmail.com", "GOOGLE"));
		targetProfile = profileRepository.save(new Profile(target, "target"));

		LinkMetadata linkMetadata = linkMetadataRepository.save(createLinkMetadata());
		notSharableBookmark = bookmarkRepository.save(createBookmark(targetProfile, linkMetadata));
		shareBookmark = bookmarkRepository.save(createBookmark(senderProfile, linkMetadata));

		// target -> sender  팔로우
		followRepository.save(new Follow(targetProfile, senderProfile));
	}

	@Test
	void 공유알림_하나_추가_성공() {
		//given
		notificationService.shareNotification(new ShareNotificationCommand(
			sender.getId(),
			targetProfile.getId(),
			shareBookmark.getId()
		));

		//when
		final List<Notification> findNotifications =
			notificationService.getNotifications(target.getId());

		//then
		//notification 검증
		assertThat(findNotifications).hasSize(1)
			.extracting(Notification::getType, Notification::getTargetProfileId)
			.containsExactly(tuple(
				NotificationType.SHARE,
				targetProfile.getId()
			));

		//notification 의 추가 정보 검증
		final Map<String, Noti> info = findNotifications.get(0).getInfo();
		assertThat(info).containsExactlyInAnyOrderEntriesOf(Map.of(
			"bookmark", new BookmarkNoti(shareBookmark.getId(), shareBookmark.getTitle(), shareBookmark.getUrl()),
			"sender", new ProfileNoti(senderProfile.getId(), senderProfile.getUsername())
		));
	}

	@Test
	void 대상이_나를_팔로우_중이_아니라면_공유_알림_추가_실패() {
		//given - target -> sender 의 illegal 한 공유 알림 커맨트
		final ShareNotificationCommand command = new ShareNotificationCommand(
			target.getId(),
			senderProfile.getId(),
			notSharableBookmark.getId()
		);

		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> notificationService.shareNotification(command));
	}

	@Test
	void 내_글이_아니면_공유_알림_추가_실패() {
		//given - sender 가 target 사용자의 게시글을 공유하는 Illegal 한 커맨드
		final ShareNotificationCommand command = new ShareNotificationCommand(
			sender.getId(),
			targetProfile.getId(),
			notSharableBookmark.getId()
		);

		//when then
		assertThatExceptionOfType(LinkoceanRuntimeException.class)
			.isThrownBy(() -> notificationService.shareNotification(command));
	}
}
