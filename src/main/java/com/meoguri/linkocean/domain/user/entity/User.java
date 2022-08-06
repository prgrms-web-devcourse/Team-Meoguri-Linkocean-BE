package com.meoguri.linkocean.domain.user.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	name = "users",
	uniqueConstraints = @UniqueConstraint(columnNames = {"email", "oauth_type"})
)
public class User extends BaseIdEntity {

	@Embedded
	private Email email;

	@Column(name = "oauth_type", nullable = false, length = 50)
	@Enumerated(STRING)
	private OAuthType oauthType;

	@Getter(NONE)
	@OneToOne(fetch = LAZY, mappedBy = "user")
	private Profile profile;

	/**
	 * 회원 가입시 사용하는 생성자
	 */
	public User(final String email, final String oauthType) {

		this.email = new Email(email);
		this.oauthType = OAuthType.of(oauthType);
	}

	public Long getProfileId() {
		return profile == null ? null : profile.getId();
	}

	/**
	 * OAuth 지원 벤더 종류
	 * GOOGLE, NAVER, KAKAO
	 */
	public enum OAuthType {

		GOOGLE, NAVER, KAKAO;

		public static OAuthType of(final String type) {
			return OAuthType.valueOf(type);
		}
	}

}
