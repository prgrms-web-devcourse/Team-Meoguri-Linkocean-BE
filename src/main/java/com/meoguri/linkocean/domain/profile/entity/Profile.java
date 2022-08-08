package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자와 관련된 정보를 저장하는 엔티티
 * - username은 필수 값이다.
 * - bio(자기 소개), image(프로필 이미지)는 없어도 된다.
 */
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

	/* FavoriteCategory 의 생명주기는 Profile 엔티티가 관리 */
	@Getter(NONE)
	@OneToMany(mappedBy = "profile", cascade = PERSIST, orphanRemoval = true)
	private List<FavoriteCategory> favoriteCategories = new ArrayList<>();

	@Column(nullable = false, unique = true, length = MAX_PROFILE_BIO_LENGTH)
	private String username;

	/* 프로필 메시지 */
	@Column(nullable = true, length = MAX_PROFILE_BIO_LENGTH)
	private String bio;

	/* 프로필 사진 주소 */
	@Column(nullable = true, length = 255)
	private String image;

	/**
	 * 회원 가입시 사용하는 생성자
	 */
	public Profile(final User user, final String username) {
		checkCondition(Objects.nonNull(user));
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");

		this.user = user;
		this.username = username;
	}

	/**
	 * 사용자는 이름, 자기소개, 프로필 이미지를 변경할 수 있다
	 */
	public void update(final String username, final String bio, final String image) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");
		checkNullableStringLength(bio, MAX_PROFILE_BIO_LENGTH, "프로필 메시지가 옳바르지 않습니다");
		checkNullableStringLength(image, MAX_PROFILE_IMAGE_URL_LENGTH, "프로필 사진 주소가 옳바르지 않습니다");

		this.username = username;
		this.bio = bio;
		this.image = image;
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
			.map(FavoriteCategory::getCategoryName)
			.collect(toList());
	}

	/**
	 * 선호 카테고리 목록 업데이트
	 */
	public void updateFavoriteCategories(final List<String> categories) {
		// 기존 목록 중 업데이트 목록에 없다면 삭제
		favoriteCategories.removeIf(fc -> !categories.contains(fc.getCategory().getKorName()));

		// 업데이트 목록 중 기존 목록에 포함되지 않았으면 추가
		categories.stream()
			.filter(c -> !favoriteCategories.stream()
				.map(FavoriteCategory::getCategoryName)
				.collect(toList()).contains(c))
			.forEach(c -> favoriteCategories.add(new FavoriteCategory(this, c)));
	}
}
