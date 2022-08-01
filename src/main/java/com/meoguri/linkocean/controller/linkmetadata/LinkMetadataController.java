package com.meoguri.linkocean.controller.linkmetadata;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.controller.linkmetadata.dto.LinkMetadataTitleResponse;
import com.meoguri.linkocean.domain.linkmetadata.service.LinkMetadataService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/linkmetadatas")
@RestController
public class LinkMetadataController {

	private final LinkMetadataService linkMetadataService;

	@PostMapping("/obtain")
	public LinkMetadataTitleResponse getLinkMetaTitle(@RequestParam("link") String link) {
		return LinkMetadataTitleResponse.of(linkMetadataService.getTitleByLink(link));
	}
}
