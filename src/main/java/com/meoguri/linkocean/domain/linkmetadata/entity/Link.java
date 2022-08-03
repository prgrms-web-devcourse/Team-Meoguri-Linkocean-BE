package com.meoguri.linkocean.domain.linkmetadata.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * url
 * 링크 메타 데이터 테이블의 column 이며 데이터 중복을 줄이기 위해
 * schema (http, https) 와 www. 을 줄여서 저장함
 */
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public final class Link {

	@Column(nullable = false, unique = true, length = 255)
	private String link;

	public Link(final String link) {
		checkArgument(link.contains("."), "link 형식이 올바르지 않습니다.");

		this.link = removeSchemaAndWwwIfExists(link);
	}

	private String removeSchemaAndWwwIfExists(final String link) {
		return link.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
	}

	public String getFullLink() {
		return "https://www." + this.link;
	}

	public String getSavedLink() {
		return this.link;
	}
}
