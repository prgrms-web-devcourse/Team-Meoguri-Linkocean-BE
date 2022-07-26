package com.meoguri.linkocean.internal.user.domain.model;

import static com.meoguri.linkocean.exception.Preconditions.*;
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

import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.support.internal.entity.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자
 * - 사용자를 등록할 때 [이메일, 소셜 타입]은 필수이다.
 */
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

	/* 고민 - profile을 User에서 참조하는게 어색하다고 느껴짐 */
	@Getter(NONE)
	@OneToOne(fetch = LAZY)
	private Profile profile;

	/* 회원 가입시 사용하는 생성자 */
	public User(final Email email, final OAuthType oauthType) {
		checkNotNull(email);
		checkNotNull(oauthType);

		this.email = email;
		this.oauthType = oauthType;
	}

	public Long getProfileId() {
		return profile == null ? null : profile.getId();
	}

	public void registerProfile(final Profile profile) {
		this.profile = profile;
	}
}
