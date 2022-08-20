package com.meoguri.linkocean.domain.notification.entity;

import static java.time.LocalDateTime.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "bookmark_id"})
)
public class GetBookmarkRecord extends BaseIdEntity {

	/* 조회 요청 사용자 프로필 아이디 */
	@Column(nullable = false, name = "profile_id")
	private long profileId;

	/* 상세 조회 대상 북마크 아이디 */
	@Column(nullable = false, name = "bookmark_id")
	private long bookmarkId;

	/* 조회 일시 */
	@Column(nullable = false, name = "get_at")
	private LocalDateTime getAt;

	public GetBookmarkRecord(final long profileId, final long bookmarkId) {
		this.profileId = profileId;
		this.bookmarkId = bookmarkId;
		this.getAt = now();
	}

	public void updateGetAt() {
		this.getAt = now();
	}
}
