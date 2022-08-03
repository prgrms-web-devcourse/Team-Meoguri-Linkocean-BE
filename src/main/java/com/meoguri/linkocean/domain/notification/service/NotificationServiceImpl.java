package com.meoguri.linkocean.domain.notification.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.notification.entity.Noti;
import com.meoguri.linkocean.domain.notification.entity.Notification;
import com.meoguri.linkocean.domain.notification.entity.NotificationType;
import com.meoguri.linkocean.domain.notification.entity.noti.BookmarkNoti;
import com.meoguri.linkocean.domain.notification.entity.noti.ProfileNoti;
import com.meoguri.linkocean.domain.notification.persistence.NotificationRepository;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	// TODO - 팔로우 여부 검증 필요
	@Override
	public void shareNotification(ShareNotificationCommand command) {

		final long senderUserId = command.getSenderUserId();
		final long targetProfileId = command.getTargetProfileId();
		final Profile sender = findProfileByUserIdQuery.findByUserId(senderUserId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());

		Map<String, Noti> info = Map.of(
			"sender", new ProfileNoti(sender.getId(), sender.getUsername()),
			"bookmark", new BookmarkNoti(bookmark.getId(), bookmark.getTitle(), "link")
		);

		notificationRepository
			.save(new Notification(NotificationType.SHARE, targetProfileId, info));
	}
}
