package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
