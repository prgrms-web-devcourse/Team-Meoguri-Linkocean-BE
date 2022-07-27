package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
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

	@OneToMany(mappedBy = "profile")
	private List<FavoriteCategory> favoriteCategories = new ArrayList<>();

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
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");

		this.user = user;
		this.username = username;
	}

	/**
	 * 사용자는 이름, 자기소개, 프로필 이미지를 변경할 수 있다
	 */
	public void update(final String username, final String bio, final String imageUrl) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");
		checkNullableStringLength(bio, MAX_PROFILE_BIO_LENGTH, "프로필 메시지가 옳바르지 않습니다");
		checkNullableStringLength(imageUrl, MAX_PROFILE_BIO_LENGTH, "프로필 사진 주소가 옳바르지 않습니다");

		this.username = username;
		this.bio = bio;
		this.imageUrl = imageUrl;
	}

	/**
	 * 프로필 - 선호 카테고리의 연관관계 편의 메서드
	 */
	public void addToFavoriteCategory(String category) {
		this.favoriteCategories.add(new FavoriteCategory(this, category));
	}

	/**
	 * 선호카테고리 목록 조회
	 */
	public List<String> getMyFavoriteCategories() {
		return this.favoriteCategories.stream()
			.map(FavoriteCategory::getCategory)
			.collect(toList());
	}

	/**
	 * 선호 카테고리 목록 업데이트
	 */
	public void updateFavoriteCategories(final List<String> categories) {
		this.favoriteCategories = categories.stream()
			.map(c -> new FavoriteCategory(this, c))
			.collect(toList());
	}
}
