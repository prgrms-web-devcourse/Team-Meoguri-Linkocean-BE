package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.domain.bookmark.entity.Bookmark.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 선호 북마크 카테고리
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
		this.profile = profile;
		this.category = Category.of(categoryName);
	}

	public String getCategoryName() {
		return this.category.getName();
	}

}
