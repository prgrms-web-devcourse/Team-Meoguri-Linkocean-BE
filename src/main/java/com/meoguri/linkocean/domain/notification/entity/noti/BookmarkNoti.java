package com.meoguri.linkocean.domain.notification.entity.noti;

import com.meoguri.linkocean.domain.notification.entity.Noti;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookmarkNoti implements Noti {

	private final long bookmarkId;
	private final String title;
	private final String link;
}
