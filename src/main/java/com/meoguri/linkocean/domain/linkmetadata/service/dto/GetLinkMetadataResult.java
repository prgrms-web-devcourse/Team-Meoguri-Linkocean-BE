package com.meoguri.linkocean.domain.linkmetadata.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetLinkMetadataResult {

	private final String title;
	private final String image;
}
