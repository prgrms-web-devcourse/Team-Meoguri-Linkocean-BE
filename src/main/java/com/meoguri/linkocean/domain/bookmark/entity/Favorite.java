package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기
 * - 사용자는 즐겨찾기를 북마크당 하나만 가질 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"bookmark_id", "owner_id"})
)
public class Favorite extends BaseIdEntity {

	@ManyToOne(fetch = LAZY, optional = false)
	private Bookmark bookmark;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "owner_id")
	private Profile profile;

	public Favorite(final Bookmark bookmark, final Profile profile) {
		checkNotNull(bookmark);
		checkNotNull(profile);

		this.bookmark = bookmark;
		this.profile = profile;
	}
}
