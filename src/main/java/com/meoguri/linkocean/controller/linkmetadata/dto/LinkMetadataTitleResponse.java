package com.meoguri.linkocean.controller.linkmetadata.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LinkMetadataTitleResponse {

	private final String title;

	public static LinkMetadataTitleResponse of(final String title) {
		return new LinkMetadataTitleResponse(title);
	}

}
