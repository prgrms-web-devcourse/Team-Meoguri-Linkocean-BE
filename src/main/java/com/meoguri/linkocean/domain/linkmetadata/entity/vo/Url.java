package com.meoguri.linkocean.domain.linkmetadata.entity.vo;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Url {

	private static final String URL_REGEX_WITH_HTTP_OR_HTTPS
		= "^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\"
		+ ".[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$";
	private static final String URL_REGEX_WITHOUT_HTTP_OR_HTTPS
		= "^[-a-zA-Z0-9@:%._+~#=]{1,256}\\"
		+ ".[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$";
	private static final Pattern URL_PATTERN_WITH_HTTP_OR_HTTPS = Pattern.compile(URL_REGEX_WITH_HTTP_OR_HTTPS);
	private static final Pattern URL_PATTERN_WITHOUT_HTTP_OR_HTTPS = Pattern.compile(URL_REGEX_WITHOUT_HTTP_OR_HTTPS);

	@Column(nullable = false, unique = true)
	private String url;

	public Url(final String url) {
		checkArgument(
			URL_PATTERN_WITH_HTTP_OR_HTTPS.matcher(url).matches()
				|| URL_PATTERN_WITHOUT_HTTP_OR_HTTPS.matcher(url).matches(),
			"url 형식이 잘못 되었습니다."
		);

		this.url = removeSchemaAndWwwIfExists(url);
	}

	private String removeSchemaAndWwwIfExists(final String url) {
		return url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
	}

	/**
	 * https와 www를 prefix로 추가해 url의 문자열을 반환하는 API
	 */
	public String getUrlWithSchemaAndWww() {
		return addSchemaAndWww(this.url);
	}

	private String addSchemaAndWww(final String reducedLink) {
		return "https://www." + reducedLink;
	}

	public static String toString(final Url url) {
		return url.url;
	}
}
