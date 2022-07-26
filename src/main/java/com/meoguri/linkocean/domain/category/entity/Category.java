package com.meoguri.linkocean.domain.category.entity;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 카테고리
 * - 초기 세팅된 12개의 카테고리만 존재 하기 때문에 별도의 '카테고리 추가'와 같은 기능을 구현하지 않음
 * - 이에 따라 public 생성자를 추가하지 않음
 *
 * - See resources/sql/InsertCategories.sql
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Category extends BaseIdEntity {

	public static final int CATEGORY_NAME_MAX_LENGTH = 50;

	@Column(nullable = false, unique = true, length = CATEGORY_NAME_MAX_LENGTH)
	private String name;

}
