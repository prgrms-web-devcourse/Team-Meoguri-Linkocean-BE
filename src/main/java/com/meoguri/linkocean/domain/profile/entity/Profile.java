package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필
 * - 프로필을 [사용자,유저 이름]으로 등록할 수 있다 추가로 [선호 카테고리]를 등록할 수 있다.
 * - 프로필의 [유저 이름]은 중복 될 수 없다.
 * - 프로필의 [유저 이름, 자기 소개, 이미지, 선호 카테고리 목록]을 수정할 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Profile extends BaseIdEntity {

	public static final int MAX_PROFILE_USERNAME_LENGTH = 50;
	public static final int MAX_PROFILE_BIO_LENGTH = 50;
	public static final int MAX_PROFILE_IMAGE_URL_LENGTH = 255;

	@OneToOne(fetch = LAZY, mappedBy = "profile")
	private User user;

	/* FavoriteCategory 의 생명주기는 Profile 엔티티가 관리 */
	@Getter(NONE)
	@OneToMany(mappedBy = "profile", cascade = ALL, orphanRemoval = true)
	private List<FavoriteCategory> favoriteCategories = new ArrayList<>();

	@Column(nullable = false, unique = true, length = MAX_PROFILE_USERNAME_LENGTH)
	private String username;

	/* 프로필 메시지 */
	@Column(nullable = true, length = MAX_PROFILE_BIO_LENGTH)
	private String bio;

	/* 프로필 사진 주소 */
	@Column(nullable = true, length = 700)
	private String image;

	/* 회원 가입시 사용하는 생성자 */
	public Profile(final User user, final String username) {
		checkNotNull(user);
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");

		this.user = user;
		this.username = username;
	}

	public Profile(final String username, final List<Category> categories) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");

		this.username = username;
		updateFavoriteCategories(categories);
	}

	/* 사용자는 이름, 자기소개, 프로필 이미지를 변경할 수 있다 */
	public void update(final String username, final String bio, final String image) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");
		checkNullableStringLength(bio, MAX_PROFILE_BIO_LENGTH, "프로필 메시지가 옳바르지 않습니다");
		checkNullableStringLength(image, MAX_PROFILE_IMAGE_URL_LENGTH, "프로필 사진 주소가 옳바르지 않습니다");

		this.username = username;
		this.bio = bio;
		this.image = image;
	}

	/* 선호카테고리 목록 조회 */
	public List<Category> getMyFavoriteCategories() {
		return this.favoriteCategories.stream()
			.map(FavoriteCategory::getCategory)
			.collect(toList());
	}

	/* 선호 카테고리 목록 업데이트 */
	public void updateFavoriteCategories(final List<Category> categories) {
		/* 기존 목록 중 업데이트 목록에 없다면 삭제 */
		favoriteCategories.removeIf(fc -> !categories.contains(fc.getCategory()));

		/* 업데이트 목록 중 기존 목록에 포함되지 않았으면 추가 */
		categories.stream()
			.filter(c -> !favoriteCategories.stream()
				.map(FavoriteCategory::getCategory)
				.collect(toList()).contains(c))
			.forEach(c -> favoriteCategories.add(new FavoriteCategory(this, c)));
	}
}
