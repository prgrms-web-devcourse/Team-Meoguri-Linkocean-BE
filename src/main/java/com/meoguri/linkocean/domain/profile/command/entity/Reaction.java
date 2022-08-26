package com.meoguri.linkocean.domain.profile.command.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.profile.command.entity.vo.ReactionType;

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

	@Column(nullable = false, name = "bookmark_id")
	private long bookmarkId;

	@Getter
	@Enumerated(STRING)
	private ReactionType type;

	public Reaction(final Bookmark bookmark, final ReactionType type) {
		checkNotNull(bookmark);
		checkNotNull(type);

		this.bookmarkId = bookmark.getId();
		this.type = type;
	}

	public boolean isOf(final Bookmark bookmark) {
		return bookmarkId == bookmark.getId();
	}

	public void updateType(final ReactionType reactionType) {
		this.type = reactionType;
	}
}
