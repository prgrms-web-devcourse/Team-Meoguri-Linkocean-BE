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

		this.url = url;
	}

	public static String toString(final Url url) {
		return url.url;
	}
}
