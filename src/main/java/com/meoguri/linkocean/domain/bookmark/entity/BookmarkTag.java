package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 - 태그
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BookmarkTag extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Bookmark bookmark;

	@ManyToOne(fetch = LAZY)
	private Tag tag;

	public BookmarkTag(final Bookmark bookmark, final Tag tag) {
		checkNotNull(bookmark);
		checkNotNull(tag);

		this.bookmark = bookmark;
		this.tag = tag;
	}

	public String getTagName() {
		return tag.getName();
	}
}
