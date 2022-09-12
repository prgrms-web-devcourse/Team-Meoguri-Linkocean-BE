package com.meoguri.linkocean.domain.tag.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.meoguri.linkocean.support.domain.entity.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  태그
 * - 태그를 생성할 때 [이름]이 존재해야 한다.
 * - 태그의 이름은 유일하다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "tag")
@Entity
public class Tag extends BaseIdEntity {

	public static final int MAX_TAG_NAME_LENGTH = 50;

	@Column(nullable = false, unique = true, length = MAX_TAG_NAME_LENGTH)
	private String name;

	public Tag(final String name) {
		checkNotNullStringLength(name, MAX_TAG_NAME_LENGTH, "태그의 형식이 옳바르지 않습니다");

		this.name = name;
	}
}
