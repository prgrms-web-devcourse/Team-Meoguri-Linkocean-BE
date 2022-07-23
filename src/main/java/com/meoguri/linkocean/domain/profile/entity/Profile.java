package com.meoguri.linkocean.domain.profile.entity;

import static com.google.common.base.Preconditions.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.meoguri.linkocean.domain.common.BaseIdEntity;
import com.meoguri.linkocean.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Profile extends BaseIdEntity {

	public static final int MAX_PROFILE_USERNAME_LENGTH = 50;
	public static final int MAX_PROFILE_BIO_LENGTH = 50;
	public static final int MAX_PROFILE_IMAGE_URL_LENGTH = 255;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, unique = true, length = MAX_PROFILE_BIO_LENGTH)
	private String username;

	/* 프로필 메시지 */
	@Column(nullable = true, length = MAX_PROFILE_BIO_LENGTH)
	private String bio;

	/* 프로필 사진 주소 */
	@Column(nullable = true, length = 255)
	private String imageUrl;

	/**
	 * 회원 가입시 사용하는 생성자
	 */
	public Profile(final User user, final String username) {
		checkUsername(username);

		this.user = user;
		this.username = username;
	}

	/**
	 * 사용자는 이름, 자기소개, 프로필 이미지를 변경할 수 있다
	 */
	public void update(final String username, final String bio, final String imageUrl) {
		checkUsername(username);
		checkArgument(Objects.nonNull(bio) && bio.length() <= MAX_PROFILE_BIO_LENGTH,
			String.format("사용자 이름의 길이는 %d 보다 작아야 합니다.", MAX_PROFILE_BIO_LENGTH));
		checkArgument(Objects.nonNull(imageUrl) && imageUrl.length() <= MAX_PROFILE_IMAGE_URL_LENGTH,
			String.format("프로필 이미지 주소의 길이는 %d보다 작아야 합니다.", MAX_PROFILE_IMAGE_URL_LENGTH));

		this.username = username;
		this.bio = bio;
		this.imageUrl = imageUrl;
	}

	private void checkUsername(final String username) {
		checkArgument(hasText(username), "사용자 이름은 공백이 될 수 없습니다.");
		checkArgument(username.length() <= MAX_PROFILE_USERNAME_LENGTH,
			String.format("사용자 이름의 길이는 %d보다 작아야 합니다.", MAX_PROFILE_USERNAME_LENGTH));
	}
}
