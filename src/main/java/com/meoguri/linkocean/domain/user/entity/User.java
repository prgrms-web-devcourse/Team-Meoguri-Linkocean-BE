package com.meoguri.linkocean.domain.user.entity;

import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseIdEntity {

	@Embedded
	private Email email;

	@Column(name = "oauth_type", nullable = false, length = 50)
	@Enumerated(STRING)
	private OAuthType oAuthType;

	/**
	 * 회원 가입시 사용하는 생성자
	 */
	public User(final String email, final String oAuthType) {

		this.email = new Email(email);
		this.oAuthType = OAuthType.of(oAuthType);
	}
}
