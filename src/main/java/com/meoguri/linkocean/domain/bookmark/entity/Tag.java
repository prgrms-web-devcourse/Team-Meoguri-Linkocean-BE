package com.meoguri.linkocean.domain.bookmark.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
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
