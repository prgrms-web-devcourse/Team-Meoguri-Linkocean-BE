package com.meoguri.linkocean.domain.notification.entity;

import static java.time.LocalDateTime.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 공유 알림
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Notification extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Bookmark bookmark;

	/* 알림 대상 */
	@ManyToOne(fetch = LAZY)
	private Profile target;

	private LocalDateTime createdAt;

	public Notification(final Bookmark bookmark, final Profile target) {

		this.bookmark = bookmark;
		this.target = target;
		this.createdAt = now();
	}
}
