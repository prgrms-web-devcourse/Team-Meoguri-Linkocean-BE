package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.lang.String.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private static final int MAX_FAVORITE_CATEGORIES_SIZE = Category.values().length;
	@ElementCollection
	@CollectionTable(name = "favorite_category")
	@Column(name = "category")
	@Enumerated(STRING)
	private Set<Category> favoriteCategories = new HashSet<>();

	public FavoriteCategories(final List<Category> favoriteCategories) {
		checkCondition(favoriteCategories.size() >= 1 && favoriteCategories.size() <= MAX_FAVORITE_CATEGORIES_SIZE,
			format("category size must be in between 1 & %d", MAX_FAVORITE_CATEGORIES_SIZE));

		this.favoriteCategories = new HashSet<>(favoriteCategories);
	}

	public static List<Category> toCategories(final FavoriteCategories favoriteCategories) {
		return new ArrayList<>(favoriteCategories.favoriteCategories);
	}
}
