package com.meoguri.linkocean.domain.category.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 카테고리
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Category extends BaseIdEntity {

	public static final int CATEGORY_NAME_MAX_LENGTH = 50;

	@Column(nullable = false, unique = true, length = CATEGORY_NAME_MAX_LENGTH)
	private String name;
}
