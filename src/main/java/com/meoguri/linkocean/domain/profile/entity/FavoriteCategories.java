package com.meoguri.linkocean.domain.profile.entity;

import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class FavoriteCategories {

	@ElementCollection
	@CollectionTable(name = "favorite_category")
	@Column(name = "category")
	@Enumerated(STRING)
	private List<Category> favoriteCategories = new ArrayList<>();

	public FavoriteCategories(final List<Category> favoriteCategories) {
		// TODO - uncomment below after remove all deprecated constructor below
		// checkCondition(categories.size() >= 1 && categories.size() <= 12, "category size must be in between 1 & 12");

		this.favoriteCategories = favoriteCategories;
	}

	public static List<Category> toCategories(final FavoriteCategories favoriteCategories) {
		return favoriteCategories.favoriteCategories;
	}
}
