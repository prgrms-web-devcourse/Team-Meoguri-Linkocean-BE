package com.meoguri.linkocean.domain.profile.persistence.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindProfileCond {

	private final int page;
	private final int size;
	private final String username;
}
