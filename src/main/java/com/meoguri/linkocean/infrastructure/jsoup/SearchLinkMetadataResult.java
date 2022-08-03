package com.meoguri.linkocean.infrastructure.jsoup;

import static com.meoguri.linkocean.infrastructure.jsoup.JsoupLinkMetadataService.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLinkMetadataResult {

	private final String title;
	private final String image;

	public boolean isValid() {
		return !(title.equals(DEFAULT_TITLE) && image.equals(DEFAULT_IMAGE));
	}
}
