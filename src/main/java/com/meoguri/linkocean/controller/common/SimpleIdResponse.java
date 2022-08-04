package com.meoguri.linkocean.controller.common;

import static lombok.AccessLevel.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 엔티티 등록시 응답으로 사용하는 id 필드 만을 가지는 dto
 * 사용위치 - 북마크 등록, 프로필 등록
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class SimpleIdResponse {

	private final long id;

	public static SimpleIdResponse of(long id) {
		return new SimpleIdResponse(id);
	}
}
