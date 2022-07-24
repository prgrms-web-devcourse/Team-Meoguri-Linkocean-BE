package com.meoguri.linkocean.infrastructure.jsoup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLinkMetadataResult {

	private final String title;
	private final String imageUrl;
}