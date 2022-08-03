package com.meoguri.linkocean.domain.linkmetadata.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class LinkMetadata extends BaseIdEntity {

	@Embedded
	private Link link;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String image;

	public LinkMetadata(final String link, final String title, final String image) {

		this.link = new Link(link);
		this.title = title;
		this.image = image;
	}

	/**
	 * 링크 메타 데이터 업데이트를 위한 API
	 */
	public void update(final String title, final String image) {

		this.title = title;
		this.image = image;
	}

	public String getFullLink() {
		return link.getFullLink();
	}

	public String getSavedLink() {
		return link.getSavedLink();
	}
}
