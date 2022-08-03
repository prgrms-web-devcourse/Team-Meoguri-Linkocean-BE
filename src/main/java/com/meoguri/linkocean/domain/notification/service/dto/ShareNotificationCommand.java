package com.meoguri.linkocean.domain.notification.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShareNotificationCommand {

	private final long senderUserId;
	private final long targetProfileId;
	private final long bookmarkId;
}
