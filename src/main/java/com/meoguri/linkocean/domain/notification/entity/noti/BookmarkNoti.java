package com.meoguri.linkocean.domain.notification.entity.noti;

import com.meoguri.linkocean.domain.notification.entity.Noti;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public final class BookmarkNoti implements Noti {

	private final long bookmarkId;
	private final String title;
	private final String url;
}
