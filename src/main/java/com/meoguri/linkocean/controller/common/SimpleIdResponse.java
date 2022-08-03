package com.meoguri.linkocean.controller.common;

import static lombok.AccessLevel.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class SimpleIdResponse {

	private final long id;

	public static SimpleIdResponse of(long id) {
		return new SimpleIdResponse(id);
	}
}
