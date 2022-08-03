package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.BookmarkRepository;
import com.meoguri.linkocean.domain.linkmetadata.entity.LinkMetadata;
import com.meoguri.linkocean.domain.linkmetadata.persistence.LinkMetadataRepository;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.persistence.NotificationRepository;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class NotificationServiceImplTest {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private LinkMetadataRepository linkMetadataRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private User sender;
	private User target;

	private Profile senderProfile;
	private Profile targetProfile;

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
		shareBookmark = bookmarkRepository.save(createBookmark(senderProfile, linkMetadata));
	}

	//TODO - 팔로우 검증 추가 이후에는 팔로우 가 아니므로 테스트 깨져야함
	//     - 알림 조회 구현 이후에 리포지토리가 아닌 서비스로 검증해야함
	@Test
	void 공유알림_하나_추가_성공() {
		//given
		notificationService.shareNotification(
			new ShareNotificationCommand(
				sender.getId(),
				targetProfile.getId(),
				shareBookmark.getId()
			)
		);

		//when
		final List<Notification> findNotifications =
			notificationRepository.findByTargetProfileId(targetProfile.getId());

		//then
		assertThat(findNotifications).hasSize(1);
	}
}
