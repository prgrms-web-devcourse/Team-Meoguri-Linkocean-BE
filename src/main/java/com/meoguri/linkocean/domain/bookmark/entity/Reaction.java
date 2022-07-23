package com.meoguri.linkocean.domain.bookmark.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Reaction extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Bookmark bookmark;

	@Enumerated(STRING)
	private ReactionType type;

	public Reaction(final Bookmark bookmark, final ReactionType type) {

		this.bookmark = bookmark;
		this.type = type;
	}

	public enum ReactionType {

		LIKE,
		HATE
	}
}
