package com.meoguri.linkocean.controller.linkmetadata;

import static com.meoguri.linkocean.support.controller.dto.Default.*;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.internal.linkmetadata.service.LinkMetadataService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/linkmetadatas")
@RestController
public class LinkMetadataController {

	private final LinkMetadataService linkMetadataService;

	/* 링크 메타데이터 얻기 */
	@PostMapping("/obtain")
	public Map<String, Object> obtainTitle(
		final @RequestBody Map<String, String> request
	) {
		final String title = linkMetadataService.obtainTitle(request.get("url"));
		return Map.of("title", LINK_METADATA_TITLE.getText(title));
	}
}
