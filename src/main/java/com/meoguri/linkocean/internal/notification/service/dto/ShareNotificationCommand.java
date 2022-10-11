package com.meoguri.linkocean.internal.notification.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShareNotificationCommand {

	private final long senderProfileId;
	private final long receiverProfileId;
	private final long bookmarkId;
}
