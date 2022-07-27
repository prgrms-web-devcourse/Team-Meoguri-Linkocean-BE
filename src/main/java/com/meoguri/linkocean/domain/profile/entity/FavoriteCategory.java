package com.meoguri.linkocean.domain.profile.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Entity
public class FavoriteCategory extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@Enumerated(STRING)
	private Category category;

	public FavoriteCategory(final Profile profile, final String categoryName) {
		this.profile = profile;
		this.category = Category.valueOf(categoryName.toUpperCase());
	}

	public Profile getProfile() {
		return profile;
	}

	public String getCategory() {
		return category.lowerCase();
	}

	@RequiredArgsConstructor
	enum Category {
		SELF_DEVELOPMENT("자기계발"),
		HUMANITIES("인문"),
		POLITICES("정치"),
		SOCIAL("사회"),
		ART("예술"),
		SCIENCE("과학"),
		TECHNOLOGY("기술"),
		IT("IT"),
		HOME("가정"),
		HEALTH("건강"),
		TRAVEL("여행"),
		COOKING("요리");

		private final String name;

		String lowerCase() {
			return name().toLowerCase();
		}
	}

}
