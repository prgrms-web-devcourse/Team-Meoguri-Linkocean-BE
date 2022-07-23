package com.meoguri.linkocean.domain.user.entity;

import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.meoguri.linkocean.domain.common.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseIdEntity {

	@Embedded
	private Email email;

	@Column(length = 50)
	@Enumerated(STRING)
	private OAuthType oAuthType;

	/**
	 * 회원 가입시 사용하는 생성자
	 */
	public User(final Email email, final OAuthType oAuthType) {

		this.email = email;
		this.oAuthType = oAuthType;
	}
}
