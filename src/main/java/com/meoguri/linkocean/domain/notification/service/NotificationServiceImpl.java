package com.meoguri.linkocean.domain.notification.service;

import static com.meoguri.linkocean.exception.Preconditions.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.meoguri.linkocean.domain.profile.persistence.CheckIsFollowQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final CheckIsFollowQuery checkIsFollowQuery;

	@Override
	public void shareNotification(ShareNotificationCommand command) {
		final long senderUserId = command.getSenderUserId();
		final long targetProfileId = command.getTargetProfileId();

		final Profile sender = findProfileByUserIdQuery.findByUserId(senderUserId);
		final Profile target = findProfileByIdQuery.findById(targetProfileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(command.getBookmarkId());

		final boolean isSharable =
			checkIsFollowQuery.isFollow(target, sender) && bookmark.isOwnedBy(sender);
		checkCondition(isSharable);

		Map<String, Noti> info = Map.of(
			"sender", new ProfileNoti(sender.getId(), sender.getUsername()),
			"bookmark", new BookmarkNoti(bookmark.getId(), bookmark.getTitle(), bookmark.getUrl())
		);

		notificationRepository
			.save(new Notification(NotificationType.SHARE, targetProfileId, info));
	}

	@Override
	public List<Notification> getNotifications(final long userId) {

		final Long currentUserProfileId = findProfileByUserIdQuery.findByUserId(userId).getId();
		return notificationRepository.findByTargetProfileId(currentUserProfileId);
	}
}
