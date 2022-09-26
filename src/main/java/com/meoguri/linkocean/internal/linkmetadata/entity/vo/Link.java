package com.meoguri.linkocean.internal.linkmetadata.entity.vo;

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
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public final class Link {

	private static final String URL_REGEX = "^((http|https)://)?(www.)?([^:\\/\\s]+)(:([^\\/]*))?"
		+ "((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?([가-힣])?$";
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
}
