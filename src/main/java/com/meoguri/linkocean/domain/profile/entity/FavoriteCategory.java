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
 * - 선호 카테고리에는 [프로필, 카테고리]는 필수이다
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class FavoriteCategory extends BaseIdEntity {

	@ManyToOne(fetch = LAZY, optional = false)
	private Profile profile;

	@Column(nullable = true, length = 20)
	@Enumerated(STRING)
	private Category category;

	public FavoriteCategory(final Profile profile, final Category category) {
		checkNotNull(profile);
		checkNotNull(category);

		this.profile = profile;
		this.category = category;
	}
}
