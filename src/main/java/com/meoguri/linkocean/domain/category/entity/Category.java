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

	/**
	 * 테스트에서만 사용하는 생성자
	 * - 현재 카테고리는 사전에 정해지는 값으로 추가를 위한 api를 두고 있지는 않음
	 * - 추후 admin 기능 개발 등을 통해 이름으로 카테고리 엔티티를 만드는 생성자 정도는
	 *   추가 될 수 있다고 판단하여 추가됨
	 */
	public Category(final String name) {
		this.name = name;
	}
}
