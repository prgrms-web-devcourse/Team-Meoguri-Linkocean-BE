package com.meoguri.linkocean.domain.user.entity;

import static lombok.AccessLevel.*;

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

	@Column(name = "email", length = 255, unique = true)
	private String email;

	public Email(final String email) {
		//TODO validate email - 추가하면 테스트도 추가하세요 안하면 죽음

		this.email = email;
	}
}
