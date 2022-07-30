package com.meoguri.linkocean.infrastructure.jsoup;

import static com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLinkMetadataResult {

	private final String title;
	private final String imageUrl;

	public boolean isInvalid() {
		return title.equals(DEFAULT_TITLE) && imageUrl.equals(DEFAULT_IMAGE);
	}
}
