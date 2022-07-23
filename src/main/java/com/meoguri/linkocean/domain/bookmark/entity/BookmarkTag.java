package com.meoguri.linkocean.domain.bookmark.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.common.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BookmarkTag extends BaseIdEntity {

	@ManyToOne
	private Bookmark bookmark;

	@ManyToOne
	private Tag tag;

	public BookmarkTag(final Bookmark bookmark, final Tag tag) {

		this.bookmark = bookmark;
		this.tag = tag;
	}
}
