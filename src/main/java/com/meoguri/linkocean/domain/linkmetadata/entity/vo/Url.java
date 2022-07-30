package com.meoguri.linkocean.domain.linkmetadata.entity.vo;

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
public class Url {

	@Column(nullable = false, unique = true, length = 255)
	private String url;

	public Url(final String url) {
		checkArgument(url.contains("."), "url 형식이 올바르지 않습니다.");

		this.url = removeSchemaAndWwwIfExists(url);
	}

	private String removeSchemaAndWwwIfExists(final String url) {
		return url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
	}

	public String getUrlWithSchemaAndWww() {
		return "https://www." + this.url;
	}

	public static String toString(final Url url) {
		return url.url;
	}
}
