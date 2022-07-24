package com.meoguri.linkocean.domain.bookmark.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Url;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class LinkMetadata extends BaseIdEntity {

	@Embedded
	private Url url;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String imageUrl;

	public LinkMetadata(final String link, final String title, final String imageUrl) {

		this.url = new Url(link);
		this.title = title;
		this.imageUrl = imageUrl;
	}
}