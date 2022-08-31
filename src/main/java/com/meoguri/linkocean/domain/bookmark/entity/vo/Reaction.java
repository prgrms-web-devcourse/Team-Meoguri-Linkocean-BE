package com.meoguri.linkocean.domain.bookmark.entity.vo;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크에 대한 리액션
 * - 리액션을 등록할 때 [프로필, 북마크, 리액션 타입]이 존재해야 한다.
 * - 리액션 타입은 LIKE/HATE 둘 중 하나이다.
 * - 사용자는 북마크의 리액션을 변경할 수 있다.
 */
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Reaction {

	@Column(nullable = false, name = "profile_id")
	private long profileId;

	@Getter
	@Enumerated(STRING)
	private ReactionType type;

	public Reaction(final long profileId, final ReactionType type) {
		checkNotNull(type);

		this.profileId = profileId;
		this.type = type;
	}

	public boolean isOf(final long profileId) {
		return this.profileId == profileId;
	}

	public void updateType(final ReactionType reactionType) {
		this.type = reactionType;
	}
}
