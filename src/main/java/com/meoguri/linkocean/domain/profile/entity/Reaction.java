package com.meoguri.linkocean.domain.profile.entity;

import static javax.persistence.EnumType.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;

@Embeddable
public class Reaction {

	@Column(nullable = false, name = "bookmark_id")
	private long bookmarkId;

	@Enumerated(STRING)
	private ReactionType type;
}
