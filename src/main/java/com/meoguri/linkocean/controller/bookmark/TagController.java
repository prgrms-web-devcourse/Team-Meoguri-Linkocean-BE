package com.meoguri.linkocean.controller.bookmark;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.bookmark.dto.GetMyTagsResponse;
import com.meoguri.linkocean.controller.common.PageResponse;
import com.meoguri.linkocean.domain.bookmark.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@RestController
public class TagController {

	private final TagService tagService;

	@GetMapping
	public PageResponse<GetMyTagsResponse> getMyTags(@AuthenticationPrincipal SecurityUser user) {
		final List<GetMyTagsResponse> tags = tagService.getMyTags(user.getId()).stream()
			.map(GetMyTagsResponse::ofResult)
			.collect(toList());

		return PageResponse.of("tags", tags);
	}
}
