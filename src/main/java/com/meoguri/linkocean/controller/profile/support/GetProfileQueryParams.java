package com.meoguri.linkocean.controller.profile.support;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GetProfileQueryParams {

	private final int page;
	private final int size;
	private final String username;
}
