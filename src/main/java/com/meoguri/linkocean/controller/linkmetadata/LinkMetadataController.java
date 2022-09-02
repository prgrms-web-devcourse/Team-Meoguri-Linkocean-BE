package com.meoguri.linkocean.controller.linkmetadata;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.domain.linkmetadata.service.LinkMetadataService;

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
		String title = linkMetadataService.obtainTitle(request.get("url"));
		return Map.of("title", title == null ? "제목 없음" : title);
	}
}
