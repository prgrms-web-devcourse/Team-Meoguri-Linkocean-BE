package com.meoguri.linkocean.domain.user.entity.vo;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Email {

	private static final String EMAIL_REGEX
		= "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*"
		+ "@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)"
		+ "+[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

	@Column(name = "email", length = 255)
	private String email;

	public Email(final String email) {
		checkArgument(EMAIL_PATTERN.matcher(email).find(), "이메일 형식이 잘못 되었습니다.");

		this.email = email;
	}

	public static String toString(final Email email) {
		return email.email;
	}
}
