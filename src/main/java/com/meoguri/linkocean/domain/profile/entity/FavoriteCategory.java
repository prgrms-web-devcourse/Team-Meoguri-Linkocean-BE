package com.meoguri.linkocean.domain.profile.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class FavoriteCategory extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Profile profile;

	@Enumerated(STRING)
	private Category category;

	public FavoriteCategory(final Profile profile, final String categoryName) {
		this.profile = profile;
		this.category = Category.of(categoryName);
	}

	public String getCategory() {
		return category.getName();
	}

	enum Category {

		SELF_DEVELOPMENT, //자기계발
		HUMANITIES, //인문
		POLITICS, //정치
		SOCIAL, //사회
		ART, //예술
		SCIENCE, //과학
		TECHNOLOGY, //기술
		IT, //IT
		HOME, //가정
		HEALTH, //건강
		TRAVEL, //여행
		COOKING; //요리

		String getName() {
			return name().toLowerCase();
		}

		static Category of(String arg) {
			return Category.valueOf(arg.toUpperCase());
		}
	}

}
