package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class LinkMetadataResult {

	private final String imageUrl;
	private final String title;
}
