package com.meoguri.linkocean.domain.linkmetadata.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 링크의 메타데이터
 *
 * - 링크 메타데이터를 등록할 때 [링크, 제목, 이미지]가 존재해야 한다.
 * - 링크 메타데이터는 동일한  [link]에 대해서 [제목, 이미지]를 수정할 수 있다.
 */
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
