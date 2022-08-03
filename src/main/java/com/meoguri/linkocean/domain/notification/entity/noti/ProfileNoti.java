package com.meoguri.linkocean.domain.notification.entity.noti;

import com.meoguri.linkocean.domain.notification.entity.Noti;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileNoti implements Noti {

	private final long profileId;
	private final String profileUsername;
}
