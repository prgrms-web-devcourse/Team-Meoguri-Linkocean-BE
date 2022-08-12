package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.entity.NotificationType;
import com.meoguri.linkocean.domain.notification.persistence.NotificationRepository;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;
	private final CheckIsFollowQuery checkIsFollowQuery;

	/**
	 * 북마크 공유 알림 생성
	 * - 나를 팔로우 해주는 상대에게만 공유 할 수 있다.
	 * - 자신의 북마크만 공유할 수 있다.
	 */
	@Transactional
	@Override
	public void shareNotification(final ShareNotificationCommand command) {
		/* 수신자 조회 */
		final Profile receiver = findProfileByIdQuery.findById(command.getReceiverProfileId());

		/* 추가 정보 조회 */
		final Profile sender = findProfileByIdQuery.findById(command.getSenderProfileId());
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());

		/* 비즈니스 로직 검사 */
		final boolean isSenderFollowedByReceiver = checkIsFollowQuery.isFollow(receiver.getId(), sender);
		final boolean isWriter = bookmark.isWrittenBy(sender);
		checkCondition(isSenderFollowedByReceiver && isWriter, "illegal share command");

		/* 공유 알림 저장 */
		final Notification shareNotification = new Notification(
			NotificationType.SHARE,
			receiver,
			Map.of(
				"bookmark", Map.of(
					"id", bookmark.getId(),
					"title", bookmark.getTitle(),
					"link", bookmark.getUrl()
				),
				"sender", Map.of(
					"id", sender.getId(),
					"username", sender.getUsername()
				))
		);

		notificationRepository.save(shareNotification);
	}

	@Override
	public Slice<Notification> getNotifications(final Pageable pageable) {
		return notificationRepository.findBy(pageable);
	}

}
