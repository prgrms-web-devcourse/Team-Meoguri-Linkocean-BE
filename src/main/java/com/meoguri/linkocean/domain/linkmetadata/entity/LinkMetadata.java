package com.meoguri.linkocean.domain.linkmetadata.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.linkmetadata.entity.vo.Url;

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

	/**
	 * 링크 메타 데이터 업데이트를 위한 API
	 */
	public void update(final String title, final String imageUrl) {

		this.title = title;
		this.imageUrl = imageUrl;
	}

	public String getFullUrl() {
		return url.getUrlWithSchemaAndWww();
	}

	public String getSavedUrl() {
		return url.getSavedUrl();
	}
}
