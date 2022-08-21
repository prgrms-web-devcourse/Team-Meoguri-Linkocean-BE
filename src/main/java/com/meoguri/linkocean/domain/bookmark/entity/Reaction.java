package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크에 대한 리액션
 * - 리액션을 등록할 때 [프로필, 북마크, 리액션 타입]이 존재해야 한다.
 * - 리액션 타입은 LIKE/HATE 둘 중 하나이다.
 * - 사용자는 북마크의 리액션을 변경할 수 있다.
 */
@Deprecated
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "bookmark_id"})
)
public class Reaction extends BaseIdEntity {

	@ManyToOne(fetch = LAZY, optional = false)
	private Profile profile;

	@ManyToOne(fetch = LAZY, optional = false)
	private Bookmark bookmark;

	@Enumerated(STRING)
	private ReactionType type;

	public Reaction(final Profile profile, final Bookmark bookmark, final ReactionType reactionType) {
		checkNotNull(profile);
		checkNotNull(bookmark);
		checkNotNull(reactionType);

		this.profile = profile;
		this.bookmark = bookmark;
		this.type = reactionType;
	}

}
