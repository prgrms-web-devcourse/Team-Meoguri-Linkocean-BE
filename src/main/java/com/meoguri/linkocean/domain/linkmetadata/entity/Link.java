package com.meoguri.linkocean.domain.linkmetadata.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 링크(Link)
 * - 링크 메타 데이터 테이블의 column 이며 유일하다.
 * - 링크는 저장될 때 schema (http, https) 와 www. 를 지우고 저장한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public final class Link {

	private static final String URL_REGEX = "^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?";
	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

	@Column(nullable = false, unique = true, length = 700)
	private String link;

	public Link(final String link) {
		checkArgument(URL_PATTERN.matcher(link).matches(), "link 형식이 올바르지 않습니다.");

		this.link = link;
	}

	public static String toString(final Link link) {
		return link.getLink();
	}

	@Deprecated
	private String removeSchemaAndWwwIfExists(final String link) {
		return link.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
	}

	@Deprecated
	public String getFullLink() {
		return "https://www." + this.link;
	}

	@Deprecated
	public String getSavedLink() {
		return this.link;
	}
}
