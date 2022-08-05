package com.meoguri.linkocean.controller.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meoguri.linkocean.domain.notification.service.dto.ShareNotificationCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ShareNotificationRequest {

	@JsonProperty("targetId")
	private long targetProfileId;

	private long bookmarkId;

	public ShareNotificationCommand toCommand(long senderUserId) {
		return new ShareNotificationCommand(senderUserId, targetProfileId, bookmarkId);
	}
}
