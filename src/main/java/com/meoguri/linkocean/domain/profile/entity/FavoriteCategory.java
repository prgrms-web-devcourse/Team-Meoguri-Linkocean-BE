package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 선호 카테고리
 *
 * - 선호 카테고리를 등록할 때 [프로필, 카테고리]가 존재해야 한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class FavoriteCategory extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@Column(nullable = true, length = 20)
	@Enumerated(STRING)
	private Category category;

	public FavoriteCategory(final Profile profile, final String categoryName) {
		checkNotNull(profile);
		checkNotNull(categoryName);

		this.profile = profile;
		this.category = Category.of(categoryName);
	}

	public String getCategoryName() {
		return this.category.getKorName();
	}

}
