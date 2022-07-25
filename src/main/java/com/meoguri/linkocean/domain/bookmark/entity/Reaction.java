package com.meoguri.linkocean.domain.bookmark.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "bookmark_id", "type"})
)
public class Reaction extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@ManyToOne(fetch = LAZY)
	private Bookmark bookmark;

	@Enumerated(STRING)
	private ReactionType type;

	public Reaction(final Profile profile, final Bookmark bookmark, final ReactionType type) {

		this.profile = profile;
		this.bookmark = bookmark;
		this.type = type;
	}

	public enum ReactionType {

		/* 좋아요 👍 */
		LIKE,

		/* 싫어요 👎 */
		HATE
	}
}
