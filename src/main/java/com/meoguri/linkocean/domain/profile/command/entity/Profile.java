package com.meoguri.linkocean.domain.profile.command.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.lang.String.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static lombok.AccessLevel.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.meoguri.linkocean.domain.bookmark.persistence.dto.BookmarkFindCond;

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

	@Embedded
	private FavoriteCategories favoriteCategories = new FavoriteCategories();

	@Embedded
	private FavoriteBookmarkIds favoriteBookmarkIds = new FavoriteBookmarkIds();

	@OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "id.follower")
	private Set<Follow> follows = new HashSet<>();

	@Embedded
	private Reactions reactions = new Reactions();

	@Column(nullable = false, unique = true, length = MAX_PROFILE_USERNAME_LENGTH)
	private String username;

	/* 프로필 메시지 */
	@Column(nullable = true, length = MAX_PROFILE_BIO_LENGTH)
	private String bio;

	/* 프로필 사진 주소 */
	@Column(nullable = true, length = 700)
	private String image;

	/* 회원 가입시 사용하는 생성자 */
	public Profile(final String username, final FavoriteCategories favoriteCategories) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");

		this.username = username;
		this.favoriteCategories = favoriteCategories;
	}

	/* 사용자는 이름, 자기소개, 프로필 이미지를 변경할 수 있다 */
	public void update(
		final String username,
		final String bio,
		final String image,
		final FavoriteCategories favoriteCategories
	) {
		checkNotNullStringLength(username, MAX_PROFILE_USERNAME_LENGTH, "사용자 이름이 옳바르지 않습니다");
		checkNullableStringLength(bio, MAX_PROFILE_BIO_LENGTH, "프로필 메시지가 옳바르지 않습니다");
		checkNullableStringLength(image, MAX_PROFILE_IMAGE_URL_LENGTH, "프로필 사진 주소가 옳바르지 않습니다");

		this.username = username;
		this.bio = bio;
		this.image = image;
		this.favoriteCategories = favoriteCategories;
	}

	/* 팔로우 */
	public void follow(final Profile target) {
		checkCondition(!checkIsFollow(target),
			format("illegal follow command of profileId: %d on targetProfileId: %d", this.getId(), target.getId()));

		this.follows.add(new Follow(this, target));
	}

	/* 언팔로우 */
	public void unfollow(final Profile target) {
		checkCondition(checkIsFollow(target),
			format("illegal unfollow command of profileId: %d on targetProfileId: %d", this.getId(), target.getId()));

		this.follows.remove(new Follow(this, target));
	}

	/* 프로필 팔로우 중인지 확인 */
	public boolean checkIsFollow(final Profile profile) {
		return this.follows.stream().anyMatch(f -> f.isFolloweeOf(profile));
	}

	/* 프로필 목록 팔로우 중인지 확인 */
	public List<Boolean> checkIsFollows(final List<Profile> profiles) {
		return profiles.stream().map(
			p -> follows.stream().anyMatch(f -> f.isFolloweeOf(p))
		).collect(toList());
	}

	/* 즐겨찾기 추가 */
	public void favorite(final Bookmark bookmark) {
		favoriteBookmarkIds.favorite(bookmark);
	}

	/* 즐겨찾기 취소 */
	public void unfavorite(final Bookmark bookmark) {
		favoriteBookmarkIds.unfavorite(bookmark);
	}

	/* 북마크 즐겨찾기 여부 확인 */
	public boolean isFavoriteBookmark(final Bookmark bookmark) {
		return favoriteBookmarkIds.isFavoriteBookmark(bookmark);
	}

	/* 북마크 목록 즐겨찾기 여부 확인 */
	public List<Boolean> isFavoriteBookmarks(final List<Bookmark> bookmarks) {
		return favoriteBookmarkIds.isFavoriteBookmarks(bookmarks);
	}

	/* 리액션 요청 */
	public ReactionType requestReaction(final Bookmark bookmark, final ReactionType requestType) {
		return reactions.requestReaction(bookmark, requestType);
	}

	/**
	 * 공개 범위 조건 - 북마크 작성자와 자신의 관계에 따라 결정 된다
	 * @see BookmarkFindCond
	 */
	public OpenType getAvailableBookmarkOpenType(final Profile target) {
		if (this.equals(target)) {
			return OpenType.PRIVATE;
		} else if (this.checkIsFollow(target)) {
			return OpenType.PARTIAL;
		} else {
			return OpenType.ALL;
		}
	}

	/* 리액션 확인 */
	public Map<ReactionType, Boolean> checkReaction(final Bookmark bookmark) {
		return reactions.checkReaction(bookmark);
	}
}
