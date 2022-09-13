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
import com.meoguri.linkocean.domain.notification.entity.vo.NotificationType;
import com.meoguri.linkocean.domain.notification.persistence.NotificationRepository;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.FindProfileByIdRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final FindProfileByIdRepository findProfileByIdRepository;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	/**
	 * 북마크 공유 알림 생성
	 * - 나를 팔로우 해주는 상대에게만 공유 할 수 있다.
	 * - 전체 공유 북마크만 공유할 수 있다.
	 */
	@Transactional
	@Override
	public void shareNotification(final ShareNotificationCommand command) {
		/* 수신자 조회 */
		final Profile receiver = findProfileByIdRepository.findProfileFetchFollows(command.getReceiverProfileId());

		/* 추가 정보 조회 */
		final Profile sender = findProfileByIdRepository.findById(command.getSenderProfileId());
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());

		/* 비즈니스 로직 검사 */
		final boolean isSenderFollowedByReceiver = receiver.isFollow(sender);
		final boolean isOpenTypeAll = bookmark.isOpenTypeAll();
		checkCondition(isSenderFollowedByReceiver && isOpenTypeAll, "illegal share command");

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
	public Slice<Notification> getNotifications(final Pageable pageable, final long profileId) {
		return notificationRepository.findByReceiverId(pageable, profileId);
	}

}
